package com.simter.domain.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class InvalidPasswordFormatException extends RuntimeException{
    public InvalidPasswordFormatException() {
        super("올바른 비밀번호 형식이 아닙니다.");
    }
}
