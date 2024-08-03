package com.simter.domain.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        super("가입되지 않은 이메일이거나 비밀번호가 일치하지 않습니다.");
    }
}
