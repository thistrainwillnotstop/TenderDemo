package com.intv.tender.tenderapi;

import com.intv.tender.tenderapi.db.repository.TenderRepository;
import com.intv.tender.tenderapi.models.GetTendersByIssuerRespDTO;
import com.intv.tender.tenderapi.services.TenderService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TenderServiceTests {

    TenderRepository tenderRepository;
    TenderService tenderService;

    @BeforeEach
    public void setup() {

        tenderRepository = mock(TenderRepository.class);
        tenderService = spy(new TenderService(tenderRepository));
    }

    @Test
    public void submitOffer_validParams() {

        try {

            when(tenderRepository.submitOffer(anyInt(), anyInt(), anyString())).thenReturn(Integer.valueOf(1));

            tenderService.submitOffer(1, 1, "offerInfo");

        } catch (TenderService.ESubmitOfferError eSubmitOfferError) {
            fail();
        }


    }
    @Test
    public void submitOffer_repoFails1() {

        try {

            when(tenderRepository.submitOffer(anyInt(), anyInt(), anyString())).thenReturn(null);

            tenderService.submitOffer(1, 1, "offerInfo");

        } catch (TenderService.ESubmitOfferError eSubmitOfferError) {
            return;
        }

        fail();
    }
    @Test
    public void submitOffer_repoFails2() {

        try {

            when(tenderRepository.submitOffer(anyInt(), anyInt(), anyString())).thenReturn(Integer.valueOf(-1));

            tenderService.submitOffer(1, 1, "offerInfo");

        } catch (TenderService.ESubmitOfferError eSubmitOfferError) {
            return;
        }

        fail();
    }
    @Test
    public void acceptOffer_validParams() {

        try {

            when(tenderRepository.acceptOffer(anyInt(), anyInt())).thenReturn(Boolean.TRUE);

            tenderService.acceptOffer(1, 1);

        } catch (TenderService.EAcceptOfferError eAcceptOfferError) {
            fail();
        }
    }
    @Test
    public void acceptOffer_repoFails1() {

        try {

            when(tenderRepository.acceptOffer(anyInt(), anyInt())).thenReturn(Boolean.FALSE);

            tenderService.acceptOffer(1, 1);

        } catch (TenderService.EAcceptOfferError eAcceptOfferError) {
            return;
        }

        fail();
    }
    @Test
    public void acceptOffer_repoFails2() {

        try {

            when(tenderRepository.acceptOffer(anyInt(), anyInt())).thenReturn(null);

            tenderService.acceptOffer(1, 1);

        } catch (TenderService.EAcceptOfferError eAcceptOfferError) {
            return;
        }

        fail();
    }
    @Test
    public void createTender_validParams() {

        try {

            when(tenderRepository.createTender(anyInt(), anyString())).thenReturn(Integer.valueOf(1));

            tenderService.createTender(1, "Some tender description");

        } catch (TenderService.ECreateTenderError e) {
            fail();
        }
    }
    @Test
    public void createTender_repoFails1() {

        try {

            when(tenderRepository.createTender(anyInt(), anyString())).thenReturn(null);

            tenderService.createTender(1, "Some tender description");

        } catch (TenderService.ECreateTenderError e) {
            return;
        }

        fail();
    }
    @Test
    public void createTender_repoFails2() {

        try {

            when(tenderRepository.createTender(anyInt(), anyString())).thenReturn(-1054);

            tenderService.createTender(1, "Some tender description");

        } catch (TenderService.ECreateTenderError e) {
            return;
        }

        fail();
    }

    @Test
    public void getAllTenderOffers_validParams() {

        try {

            when(tenderRepository.getAllTenderOffers(anyInt())).thenReturn(Map.of(1, "nice offer #1", 2, "nice offer #2"));

            tenderService.getAllTenderOffers(1);

        } catch (TenderService.EFetchOffers e) {
            fail();
        }
    }
    @Test
    public void getAllTenderOffers_repoFails1() {

        try {

            when(tenderRepository.getAllTenderOffers(anyInt())).thenReturn(null);

            tenderService.getAllTenderOffers(1);

        } catch (TenderService.EFetchOffers e) {
            return;
        }
        fail();
    }
    @Test
    public void getAllTenderOffers_repoFails2() {

        try {

            when(tenderRepository.getAllTenderOffers(anyInt())).thenReturn(new HashMap<>());

            tenderService.getAllTenderOffers(1);

        } catch (TenderService.EFetchOffers e) {
            return;
        }

        fail();
    }
//
    @Test
    public void getAllTenderOffersByBidder_validParams() {

        try {

            when(tenderRepository.getAllTenderOffersByBidder(anyInt(), anyInt())).thenReturn(Map.of(1, "nice offer #1", 2, "nice offer #2"));

            tenderService.getAllTenderOffersByBidder(1, 1);

        } catch (TenderService.EFetchOffers e) {
            fail();
        }
    }
    @Test
    public void getAllTenderOffersByBidder_repoFails1() {

        try {

            when(tenderRepository.getAllTenderOffersByBidder(anyInt(), anyInt())).thenReturn(null);

            tenderService.getAllTenderOffersByBidder(1, 1);

        } catch (TenderService.EFetchOffers e) {
            return;
        }
        fail();
    }
    @Test
    public void getAllTenderOffersByBidder_repoFails2() {

        try {

            when(tenderRepository.getAllTenderOffersByBidder(anyInt(), anyInt())).thenReturn(new HashMap<>());

            tenderService.getAllTenderOffersByBidder(1, 1);

        } catch (TenderService.EFetchOffers e) {
            return;
        }

        fail();
    }
//
    @Test
    public void getAllTendersByIssuer_validParams() {

        try {

            GetTendersByIssuerRespDTO r = new GetTendersByIssuerRespDTO();
            r.setDescription("some tender blabla");
            r.setTenderId(1);
            r.setCreatedAt(LocalDateTime.now());
            r.setStatus("OPEN");
            when(tenderRepository.getAllTendersByIssuer(anyInt())).thenReturn(List.of(r));

            tenderService.getAllTendersByIssuer(1);

        } catch (TenderService.EFetchTenders e) {
            fail();
        }
    }
    @Test
    public void getAllTendersByIssuer_repoFails1() {

        try {

            when(tenderRepository.getAllTendersByIssuer(anyInt())).thenReturn(new ArrayList<>());

            tenderService.getAllTendersByIssuer(1);

        } catch (TenderService.EFetchTenders e) {
            return;
        }
        fail();
    }
    @Test
    public void getAllTendersByIssuer_repoFails2() {

        try {

            when(tenderRepository.getAllTendersByIssuer(anyInt())).thenReturn(null);

            tenderService.getAllTendersByIssuer(1);

        } catch (TenderService.EFetchTenders e) {
            return;
        }

        fail();
    }
/*
    public Integer createTender(Integer userId, String description) throws TenderService.ECreateTenderError {

        Integer tenderId = tenderRepository.createTender(userId, description);

        if(tenderId == null || tenderId < 0)
            throw new TenderService.ECreateTenderError("Error creating tender!");

        return tenderId;
    }
*/
}
