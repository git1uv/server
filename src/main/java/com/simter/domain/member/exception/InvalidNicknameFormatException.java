package com.simter.domain.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class InvalidNicknameFormatException extends RuntimeException{
    public InvalidNicknameFormatException() {
        super("올바른 닉네임 형식이 아닙니다.");
    }
}
