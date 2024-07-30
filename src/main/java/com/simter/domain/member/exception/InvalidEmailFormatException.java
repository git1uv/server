package com.simter.domain.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException() {
        super("올바른 이메일 형식이 아닙니다.");
    }
}
