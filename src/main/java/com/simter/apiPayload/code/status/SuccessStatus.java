package com.simter.apiPayload.code.status;

import com.simter.apiPayload.code.BaseCode;
import com.simter.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    _OK(HttpStatus.OK,"COMMON200","성공입니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason(){
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .build();

    }

    @Override
    public ReasonDTO getReasonHttpStatus(){
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .httpStatus(httpStatus)
                .build();
    }
}