package com.intv.tender.tenderapi;

import com.intv.tender.tenderapi.db.repository.TenderRepository;
import com.intv.tender.tenderapi.services.TenderService;
import com.intv.tender.tenderapi.util.ParamVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ParamVerifierTests {

    ParamVerifier paramVerifier;

    @BeforeEach
    public void setup() {

        paramVerifier = new ParamVerifier();
    }

    @Test
    public void verifyIntegerParam_validParams() {

        try {

            paramVerifier.verifyIntegerParam(Integer.valueOf(1));
            paramVerifier.verifyIntegerParam(Integer.valueOf(100));
            paramVerifier.verifyIntegerParam(Integer.valueOf(98774));
        }
        catch (ParamVerifier.EParamVerificationFail eParamVerificationFail) {
            fail();
        }
    }

    @Test
    public void verifyIntegerParam_invalidParam2() {

        try {

            paramVerifier.verifyIntegerParam(Integer.valueOf(-1));
        }
        catch (ParamVerifier.EParamVerificationFail eParamVerificationFail) {
            return;
        }

        fail();
    }
    @Test
    public void verifyIntegerParam_invalidParam3() {

        try {
            paramVerifier.verifyIntegerParam(Integer.valueOf(-1654));
        }
        catch (ParamVerifier.EParamVerificationFail eParamVerificationFail) {
            return;
        }

        fail();
    }
    @Test
    public void verifyIntegerParam_invalidParam4() {

        try {
            paramVerifier.verifyIntegerParam(null);
        }
        catch (ParamVerifier.EParamVerificationFail eParamVerificationFail) {
            return;
        }

        fail();
    }

    @Test
    public void verifyStringParam_invalidParam1() {

        try {

            paramVerifier.verifyStringParam(null);
        }
        catch (ParamVerifier.EParamVerificationFail eParamVerificationFail) {
            return;
        }

        fail();
    }
    @Test
    public void verifyStringParam_invalidParam2() {

        try {

            paramVerifier.verifyStringParam("");
        }
        catch (ParamVerifier.EParamVerificationFail eParamVerificationFail) {
            return;
        }

        fail();
    }

    @Test
    public void verifyStringParam_validParam() {

        try {

            paramVerifier.verifyStringParam("some param string");
        }
        catch (ParamVerifier.EParamVerificationFail eParamVerificationFail) {
            fail();
        }

    }
}
