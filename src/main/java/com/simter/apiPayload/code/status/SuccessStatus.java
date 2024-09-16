package com.simter.apiPayload.code.status;

import com.simter.apiPayload.code.BaseCode;
import com.simter.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    _OK("200","성공입니다"),

    //편지
    MAIL_LIST("200","편지 목록 조회에 성공하였습니다."),
    MAIL_GET("200","편지 조회에 성공하였습니다."),
    MAIL_CREATE("200","편지 생성에 성공하였습니다."),
    MAIL_UPDATE("200","편지 수정에 성공하였습니다."),
    MAIL_DELETE("200","편지 삭제에 성공하였습니다."),
    MAIL_STARRED("200","편지 즐겨찾기에 성공하였습니다."),

    //종이비행기
    AIRPLANE_GET("200","종이비행기 조회에 성공하였습니다."),
    AIRPLANE_CREATE("200","종이비행기 생성에 성공하였습니다."),
    //챗봇
    CHATBOT_SESSION_START("200","챗봇 종류 선택에 성공하였습니다."),
    DEFAULT_CHATBOT("200","사용자의 기본 챗봇 타입 조회에 성공하였습니다."),
    CHATBOT_SESSION_END("200","챗봇 세션 종료에 성공하였습니다."),
    CHATBOT_CHATTING("200","챗봇 응답 생성에 성공하였습니다."),
    COUNSELING_LIST("200","상담일지 조회에 성공하였습니다.");




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
                .build();
    }
}