package com.intv.tender.tenderapi.db.repository;

import com.intv.tender.tenderapi.db.generated.tables.Issuer;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static com.intv.tender.tenderapi.db.generated.tables.Bidder.BIDDER;
import static com.intv.tender.tenderapi.db.generated.tables.Issuer.ISSUER;
import static com.intv.tender.tenderapi.db.generated.tables.User.USER;
import static java.util.List.of;

@Service
public class UserRepository {

    private DataSource dataSource;

    @Autowired
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public UserDetails loadUserByUsername(String email) throws SQLException {
        Connection conn = null;
        UserDetails userDetails = null;

        try
        {
            conn = dataSource.getConnection();
            DSLContext create = DSL.using(conn, SQLDialect.H2);

            Record2<Integer, String> resultUser = create.select(USER.ID, USER.PASSWORD).from(USER).where(USER.EMAIL.eq(email)).fetchOne();

            if(resultUser == null)
                return null;

            Integer userId = resultUser.get(USER.ID);
            String password = resultUser.get(USER.PASSWORD);

            Record2<Integer, String> resultBidder = create.select(BIDDER.USER_ID, BIDDER.BIDDER_INFO).from(BIDDER).where(BIDDER.USER_ID.eq(userId)).fetchOne();

            String authority = "";

            if(resultBidder != null)
                authority = "BIDDER";
            else
            {
                Record2<Integer, String> resultIssuer = create.select(ISSUER.USER_ID, ISSUER.ISSUER_INFO).from(ISSUER).where(ISSUER.USER_ID.eq(userId)).fetchOne();

                if (resultIssuer != null)
                    authority = "ISSUER";
            }

            userDetails = new User(userId.toString(), "{noop}" + password, of(new SimpleGrantedAuthority(authority)));
           // Use additional info of bidder/issuer here
           // String bidderInfo = resultBidder.get(BIDDER.BIDDER_INFO);
           // String issuerInfo = resultBidder.get(ISSUER.ISSUER_INFO);

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(conn != null)
                conn.close();

            return userDetails;
        }
    }

}
