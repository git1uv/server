package com.simter.apiPayload.exception.handler;

import com.simter.apiPayload.code.BaseCode;
import com.simter.apiPayload.exception.GeneralException;

public class ErrorHandler extends GeneralException {

    private final BaseCode baseCode;

    public ErrorHandler(BaseCode baseCode) {
        super(baseCode);
        this.baseCode = baseCode;
    }

    public BaseCode getErrorStatus() {
        return this.baseCode;
    }
}