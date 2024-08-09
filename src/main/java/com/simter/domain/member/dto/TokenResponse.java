package com.simter.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenResponse {

    @JsonProperty("token")
    private Token token;

    @RequiredArgsConstructor
    @Getter
    public static class Token {
        @JsonProperty("grantType")
        private String grantType;

        @JsonProperty("accessToken")
        private String accessToken;

        @JsonProperty("refreshToken")
        private String refreshToken;
    }

}
