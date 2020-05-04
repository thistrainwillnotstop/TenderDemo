package com.intv.tender.tenderapi.db.repository;
import com.intv.tender.tenderapi.db.generated.tables.Tender;
import com.intv.tender.tenderapi.models.GetTendersByIssuerRespDTO;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intv.tender.tenderapi.db.generated.tables.Bidder.BIDDER;
import static com.intv.tender.tenderapi.db.generated.tables.Issuer.ISSUER;
import static com.intv.tender.tenderapi.db.generated.tables.Tender.TENDER;
import static com.intv.tender.tenderapi.db.generated.tables.TenderOffers.TENDER_OFFERS;
import static com.intv.tender.tenderapi.db.generated.tables.User.USER;
import static java.util.List.of;


@Service
public class TenderRepository {

    private DataSource dataSource;

    @Autowired
    public TenderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Integer createTender(Integer userId, String description) {

        try(Connection conn = dataSource.getConnection())
        {

            DSLContext create = DSL.using(conn, SQLDialect.H2);

            Result<Record1<Integer>> record = create.insertInto(TENDER).set(TENDER.ISSUER_ID,     userId)
                                                                       .set(TENDER.DESCRIPTION,   description)
                                                                       .set(TENDER.STATUS,        "OPEN")
                                                                       .returningResult(TENDER.ID)
                                                                       .fetch();


            if(record == null)
                return null;


            return record.get(0).value1();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Integer submitOffer(Integer tenderId, Integer userId, String offerInfo) {

        try(Connection conn = dataSource.getConnection())
        {

            DSLContext create = DSL.using(conn, SQLDialect.H2);

            conn.setAutoCommit(false);

            Record statusRecord = create.select(TENDER.STATUS).from(TENDER).where(TENDER.ID.eq(tenderId)).fetchOne();

            if(statusRecord == null || statusRecord.getValue(TENDER.STATUS).equals(ETenderStatus.CLOSED) )
            {
                conn.rollback();
                conn.setAutoCommit(true);
                return null;
            }

            Record1<Integer> record = create.insertInto(TENDER_OFFERS)
                                                        .set(TENDER_OFFERS.BIDDER_ID,     userId    )
                                                        .set(TENDER_OFFERS.TENDER_ID,     tenderId  )
                                                        .set(TENDER_OFFERS.OFFER_INFO,    offerInfo )
                                                        .set(TENDER_OFFERS.OFFER_INFO,    offerInfo )

                                                        .returningResult(TENDER.ID)

                                                        .fetchOne();


            if(record == null)
            {
                conn.rollback();
                conn.setAutoCommit(true);
                return null;
            }

            conn.commit();
            conn.setAutoCommit(true);

            return record.value1();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {


        }
        return null;
    }

    public Boolean acceptOffer(Integer userId, Integer tenderOfferId) {

        try(Connection conn = dataSource.getConnection())
        {

            DSLContext create = DSL.using(conn, SQLDialect.H2);

            conn.setAutoCommit(false);

            Record2 statusRecord = create.select(TENDER.STATUS, TENDER.ID).from(TENDER, TENDER_OFFERS)
                                        .where(TENDER.ID.eq(TENDER_OFFERS.TENDER_ID).and(
                                               TENDER_OFFERS.ID.eq(tenderOfferId).and(
                                               TENDER.ISSUER_ID.eq(userId))))
                                        .fetchOne();

            if(statusRecord == null || statusRecord.getValue(TENDER.STATUS).equals(ETenderStatus.CLOSED) )
            {
                conn.rollback();
                conn.setAutoCommit(true);
                return false;
            }

            Integer tenderId = statusRecord.getValue(TENDER.ID);

            create.update(TENDER_OFFERS).set(TENDER_OFFERS.STATUS, EOfferStatus.REJECTED.toString()).where(TENDER_OFFERS.TENDER_ID.eq(tenderId)).execute();

            int updatedRows = create.update(TENDER_OFFERS).set(TENDER_OFFERS.STATUS, EOfferStatus.ACCEPTED.toString()).where(TENDER_OFFERS.ID.eq(tenderOfferId)).execute();

            if(updatedRows != 1)
            {
                conn.rollback();
                conn.setAutoCommit(true);
                return false;
            }

            conn.commit();
            conn.setAutoCommit(true);

            return true;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            return false;

        }
    }

    public Map<Integer, String> getAllTenderOffers(Integer tenderId) {

        try(Connection conn = dataSource.getConnection())
        {

            DSLContext create = DSL.using(conn, SQLDialect.H2);

            Map<Integer, String> resultMap = create.select(TENDER_OFFERS.ID, TENDER_OFFERS.OFFER_INFO)
                                                    .from(TENDER_OFFERS)
                                                    .where(TENDER_OFFERS.TENDER_ID.eq(tenderId))
                                                    .fetchMap(TENDER_OFFERS.ID, TENDER_OFFERS.OFFER_INFO);

            if(resultMap == null)
            {
                return null;
            }

            return resultMap;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public Map<Integer, String> getAllTenderOffersByBidder(Integer bidderId, Integer tenderId) {

        try(Connection conn = dataSource.getConnection())
        {
            DSLContext create = DSL.using(conn, SQLDialect.H2);

            Map<Integer, String> resultMap = null;
            if(tenderId != null && tenderId > 0)
            {
                resultMap = create.select(TENDER_OFFERS.ID, TENDER_OFFERS.OFFER_INFO)
                        .from(TENDER_OFFERS)
                        .where(TENDER_OFFERS.TENDER_ID.eq(tenderId).and(
                                TENDER_OFFERS.BIDDER_ID.eq(bidderId)
                        ))
                        .fetchMap(TENDER_OFFERS.ID, TENDER_OFFERS.OFFER_INFO);
            }
            else
            {
                resultMap = create.select(TENDER_OFFERS.ID, TENDER_OFFERS.OFFER_INFO)
                        .from(TENDER_OFFERS)
                        .where(TENDER_OFFERS.BIDDER_ID.eq(bidderId))
                        .fetchMap(TENDER_OFFERS.ID, TENDER_OFFERS.OFFER_INFO);
            }

            if(resultMap == null)
            {
                return null;
            }

            return resultMap;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public List<GetTendersByIssuerRespDTO> getAllTendersByIssuer(Integer userId) {

        List<GetTendersByIssuerRespDTO> retVal = new ArrayList<>();

        try(Connection conn = dataSource.getConnection())
        {
            DSLContext create = DSL.using(conn, SQLDialect.H2);

               Result<Record> result = create.select()
                        .from(TENDER)
                        .where(TENDER.ISSUER_ID.eq(userId))
                        .fetch();

               for(Record r : result)
               {
                  GetTendersByIssuerRespDTO getTendersByIssuerRespDTO = new GetTendersByIssuerRespDTO();
                  getTendersByIssuerRespDTO.setTenderId(r.getValue(TENDER.ID));
                  getTendersByIssuerRespDTO.setDescription(r.getValue(TENDER.DESCRIPTION));
                  getTendersByIssuerRespDTO.setCreatedAt(r.getValue(TENDER.CREATED_AT));

                  retVal.add(getTendersByIssuerRespDTO);
               }

            return retVal;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public enum EOfferStatus
    {
        PENDING("PENDING"),
        REJECTED("REJECTED"),
        ACCEPTED("ACCEPTED");

        private String status;

        EOfferStatus(String status) {
            this.status = status;
        }
    }

    public enum ETenderStatus
    {
        OPEN("OPEN"),
        CLOSED("CLOSED");

        private String status;

        ETenderStatus(String status) {
            this.status = status;
        }
    }
}
