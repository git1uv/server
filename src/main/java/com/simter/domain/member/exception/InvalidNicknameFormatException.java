package com.simter.domain.member.exception;

public class InvalidNicknameFormatException extends RuntimeException{
    public InvalidNicknameFormatException() {
        super("올바른 닉네임 형식이 아닙니다.");
    }
}
