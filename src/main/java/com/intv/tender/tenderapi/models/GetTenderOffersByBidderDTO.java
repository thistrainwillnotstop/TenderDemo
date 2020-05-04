package com.intv.tender.tenderapi.models;

public class GetTenderOffersByBidderDTO {

    private Integer bidderId;

    public Integer getTenderId() {
        return tenderId;
    }

    public void setTenderId(Integer tenderId) {
        this.tenderId = tenderId;
    }

    private Integer tenderId;

    public Integer getBidderId() {
        return bidderId;
    }

    public void setBidderId(Integer bidderId) {
        this.bidderId = bidderId;
    }
}
