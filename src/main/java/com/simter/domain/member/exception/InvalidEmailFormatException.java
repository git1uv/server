package com.simter.domain.member.exception;

public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException() {
        super("올바른 이메일 형식이 아닙니다.");
    }
}
