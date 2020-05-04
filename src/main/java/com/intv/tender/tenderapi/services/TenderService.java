package com.intv.tender.tenderapi.services;

import com.intv.tender.tenderapi.db.repository.TenderRepository;
import com.intv.tender.tenderapi.models.GetTendersByIssuerRespDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TenderService {

    private TenderRepository tenderRepository;

    @Autowired
    public TenderService(TenderRepository tenderRepository) {
        this.tenderRepository = tenderRepository;
    }

    public void submitOffer(int tenderId, int bidderId, String offerInfo) throws ESubmitOfferError
    {
        Integer offerId = tenderRepository.submitOffer(tenderId, bidderId, offerInfo);

        if(offerId == null || offerId < 0)
            throw new ESubmitOfferError("Failed to submit offer!");

        return;
    }

    public Integer createTender(Integer userId, String description) throws ECreateTenderError {

        Integer tenderId = tenderRepository.createTender(userId, description);

        if(tenderId == null || tenderId < 0)
            throw new ECreateTenderError("Error creating tender!");

        return tenderId;
    }

    public void acceptOffer(Integer userId, Integer tenderOfferId) throws EAcceptOfferError {

        Boolean success = tenderRepository.acceptOffer(userId, tenderOfferId);

        if(success == null || !success)
            throw new EAcceptOfferError("Error accepting offer!");

        return;
    }

    public Map<Integer, String> getAllTenderOffers(Integer tenderId) throws EFetchOffers {

        Map<Integer, String> offers = tenderRepository.getAllTenderOffers(tenderId);

        if(offers == null)
            throw new EFetchOffers("Error getting offers for tender!");
        if(offers.size() == 0)
            throw new EFetchOffers("0 offers for tender!");

        return offers;
    }

    public Map<Integer, String> getAllTenderOffersByBidder(Integer bidderId, Integer tenderId) throws EFetchOffers {
        Map<Integer, String> offers = tenderRepository.getAllTenderOffersByBidder(bidderId, tenderId);

        if(offers == null)
            throw new EFetchOffers("Error getting offers for tender!");
        if(offers.size() == 0)
            throw new EFetchOffers("0 offers for tender!");

        return offers;
    }

    public List<GetTendersByIssuerRespDTO> getAllTendersByIssuer(Integer userId) throws EFetchTenders {

        List<GetTendersByIssuerRespDTO> tenders = tenderRepository.getAllTendersByIssuer(userId);

        if(tenders == null)
            throw new EFetchTenders("Error getting tenders for issuer!");
        if(tenders.size() == 0)
            throw new EFetchTenders("0 tenders for issuer!");

        return tenders;


    }


    public static class ECreateTenderError extends Exception {
        public ECreateTenderError(String msg) { super(msg); }
    }

    public static class ESubmitOfferError extends Exception {
        public ESubmitOfferError(String msg) { super(msg); }
    }

    public static class EAcceptOfferError extends Exception {
        public EAcceptOfferError(String msg) { super(msg); }
    }
    public static class EFetchOffers extends Exception {
        public EFetchOffers(String msg) { super(msg); }
    }
    public static class EFetchTenders extends Exception {
        public EFetchTenders(String msg) { super(msg); }
    }
}
