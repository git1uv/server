package com.simter.domain.member.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simter.domain.member.dto.JwtTokenDto;
import com.simter.domain.member.dto.MemberRequestDto.RegisterDto;
import com.simter.domain.member.dto.TokenResponse;
import com.simter.domain.member.entity.Member;
import io.jsonwebtoken.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class TokenConverter {
    public static JwtTokenDto convertToToken(String token) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TokenResponse tokenResponse = objectMapper.readValue(token, TokenResponse.class);
            String accessToken = tokenResponse.getToken().getAccessToken();
            String refreshToken = tokenResponse.getToken().getRefreshToken();
            return JwtTokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        } catch (IOException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
