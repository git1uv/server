package com.simter.domain.member.exception;

public class InvalidPasswordFormatException extends RuntimeException{
    public InvalidPasswordFormatException() {
        super("올바른 비밀번호 형식이 아닙니다.");
    }
}
