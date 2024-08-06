package com.simter.apiPayload.exception.handler;

import com.simter.apiPayload.code.BaseCode;
import com.simter.apiPayload.exception.GeneralException;

public class ErrorHandler extends GeneralException {

    public ErrorHandler(BaseCode baseCode) {
        super(baseCode);
    }
}