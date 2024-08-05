package com.simter.apiPayload.exception.handler;

import com.simter.apiPayload.code.BaseErrorCode;
import com.simter.apiPayload.exception.GeneralException;

public class TestHandler extends GeneralException {
    public TestHandler(BaseErrorCode errorCode){
        super(errorCode);
    }

}
