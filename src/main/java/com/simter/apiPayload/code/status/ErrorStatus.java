package com.simter.apiPayload.code.status;


import com.simter.apiPayload.code.BaseCode;
import com.simter.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //JWT
    JWT_BAD_REQUEST(HttpStatus.UNAUTHORIZED, "JWT4001", "잘못된 JWT 서명입니다."),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4002", "액세스 토큰이 만료되었습니다."),
    JWT_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4003",
            "리프레시 토큰이 만료되었습니다. 다시 로그인하시기 바랍니다."),
    JWT_UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT4004", "지원하지 않는 JWT 토큰입니다."),
    JWT_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT4005", "유효한 JWT 토큰이 없습니다."),
    JWT_TOKEN_LOGOUT(HttpStatus.BAD_REQUEST, "JWT4006", "로그아웃 처리된 토큰입니다."),


    // 멤버 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER4003", "올바른 이메일 형식이 아닙니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER4004", "올바른 비밀번호 형식이 아닙니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER4005", "올바른 닉네임 형식이 아닙니다."),
    INVALID_LOGIN(HttpStatus.BAD_REQUEST, "MEMBER4006", "가입되지 않은 이메일이거나 비밀번호가 일치하지 않습니다."),

    //종이비행기 관련 에러
    AIRPLANE_NOT_FOUND(HttpStatus.BAD_REQUEST, "PAPER_AIRPLANE4001", "종이비행기가 없습니다."),

    //메일 관련 에러
    MAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "MAIL4001", "메일이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .httpStatus(httpStatus)
                .build();
    }
}