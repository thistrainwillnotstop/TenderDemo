package com.intv.tender.tenderapi.util;

import org.springframework.stereotype.Service;

@Service
public class ParamVerifier {


    public void verifyStringParam(String param) throws EParamVerificationFail {

        if(param == null || param.length() == 0)
            throw new EParamVerificationFail("String param is empty!");

    }

    public void verifyIntegerParam(Integer param) throws EParamVerificationFail {

        if(param == null || param < 0)
            throw new EParamVerificationFail("Integer param is empty!");
    }


    public static class EParamVerificationFail extends Exception {
        public EParamVerificationFail(String msg) {
            super(msg);
        }
    }
}
