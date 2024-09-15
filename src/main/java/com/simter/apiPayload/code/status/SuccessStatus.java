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