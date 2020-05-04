package com.intv.tender.tenderapi;

import com.intv.tender.tenderapi.models.*;
import com.intv.tender.tenderapi.services.TenderService;
import com.intv.tender.tenderapi.services.TenderService.ECreateTenderError;
import com.intv.tender.tenderapi.services.TenderService.ESubmitOfferError;
import com.intv.tender.tenderapi.services.TenderService.EAcceptOfferError;
import com.intv.tender.tenderapi.services.TenderService.EFetchOffers;
import com.intv.tender.tenderapi.services.TenderService.EFetchTenders;
import com.intv.tender.tenderapi.util.*;
import com.intv.tender.tenderapi.util.ParamVerifier.EParamVerificationFail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Controller
public class TenderController {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private TenderService tenderService;
    private ParamVerifier paramVerifier;

    @Autowired
    public TenderController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, TenderService tenderService, ParamVerifier paramVerifier) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tenderService = tenderService;
        this.paramVerifier = paramVerifier;
    }

    private String getUserFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null)
        {
            User p = (User) auth.getPrincipal();
            if(p != null)
            {
                return p.getUsername();
            }
        }

        return null;
    }

    @PostMapping("/auth")
    public ResponseEntity createAuthToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception
    {
        try
        {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            String jwtToken = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
            return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
        }
        catch(BadCredentialsException e)
        {

        }
        return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping({"/createTender"})
    public ResponseEntity createTender(@RequestBody CreateTenderDTO createTenderDTO)
    {
        try
        {
            String userIdString = getUserFromSecurityContext();

            if(userIdString == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user in JWT");

            Integer userId = Integer.parseInt(userIdString);

            paramVerifier.verifyStringParam(createTenderDTO.getDescription());

            Integer tenderId = tenderService.createTender(userId, createTenderDTO.getDescription());

            return ResponseEntity.ok(Map.of("tenderId", tenderId));
        }
        catch (EParamVerificationFail e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (ECreateTenderError e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    @PostMapping({"/acceptOffer"})
    public ResponseEntity acceptOffer(@RequestBody AcceptOfferDTO acceptOfferDTO)
    {
        try
        {
            String userIdString = getUserFromSecurityContext();

            if(userIdString == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user in JWT");

            Integer userId = Integer.parseInt(userIdString);

            paramVerifier.verifyIntegerParam(acceptOfferDTO.getTenderOfferId());

            tenderService.acceptOffer(userId, acceptOfferDTO.getTenderOfferId());

            return ResponseEntity.ok(Map.of("result", "success"));
        }
        catch (EParamVerificationFail e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (EAcceptOfferError e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    @PostMapping({"/submitOffer"})
    public ResponseEntity submitOffer(@RequestBody SubmitOfferDTO submitOfferDTO)
    {
        try
        {
            String userIdString = getUserFromSecurityContext();

            if(userIdString == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user in JWT");

            Integer userId = Integer.parseInt(userIdString);

            paramVerifier.verifyIntegerParam(submitOfferDTO.getTenderId());
            paramVerifier.verifyStringParam(submitOfferDTO.getOfferInfo());


            tenderService.submitOffer(submitOfferDTO.getTenderId(), userId, submitOfferDTO.getOfferInfo());

            return ResponseEntity.ok().body("{\"result\":\"success\"}");

        }
        catch (EParamVerificationFail e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (ESubmitOfferError e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping({"/getAllTenderOffers"})
    public ResponseEntity getAllTenderOffers(@RequestBody GetAllTenderOffersDTO getAllTenderOffersDTO)
    {
        try
        {
            paramVerifier.verifyIntegerParam(getAllTenderOffersDTO.getTenderId());

            return ResponseEntity.ok().body(tenderService.getAllTenderOffers(getAllTenderOffersDTO.getTenderId()));
        }
        catch (EParamVerificationFail e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (EFetchOffers e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping({"/getAllTenderOffersByBidder"})
    public ResponseEntity getAllTenderOffersByBidder(@RequestBody GetTenderOffersByBidderDTO getTenderOffersByBidderDTO)
    {
        try
        {
            Integer tenderId = getTenderOffersByBidderDTO.getTenderId();
            Integer bidderId = getTenderOffersByBidderDTO.getBidderId();

            paramVerifier.verifyIntegerParam(getTenderOffersByBidderDTO.getBidderId());

            if(tenderId != null)
                paramVerifier.verifyIntegerParam(getTenderOffersByBidderDTO.getTenderId());

            return ResponseEntity.ok().body(tenderService.getAllTenderOffersByBidder(bidderId, tenderId));
        }
        catch (EParamVerificationFail e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (EFetchOffers e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping({"/getAllTendersByIssuer"})
    public ResponseEntity getAllTendersByIssuer()
    {
        try
        {
            String userIdString = getUserFromSecurityContext();

            if(userIdString == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user in JWT");

            Integer userId = Integer.parseInt(userIdString);

            return ResponseEntity.ok().body(tenderService.getAllTendersByIssuer(userId));
        }
        catch (EFetchTenders e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    /*
*    .antMatchers("/createTender").hasAuthority("ISSUER")
     .antMatchers("/acceptOffer").hasAuthority("ISSUER")
     .antMatchers("/submitOffer").hasAuthority("BIDDER")
     .antMatchers("/getAllTenderOffers").hasAuthority("ISSUER")
     .antMatchers("/getAllTenderOffersByBidder").hasAuthority("BIDDER")
     .antMatchers("/getAllTendersByIssuer").hasAuthority("ISSUER")
    *
    * */

}
