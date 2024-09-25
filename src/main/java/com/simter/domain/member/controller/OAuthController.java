package com.simter.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.simter.apiPayload.ApiResponse;
import com.simter.domain.member.dto.JwtTokenDto;
import com.simter.domain.member.dto.MemberRequestDto.SocialLoginDto;
import com.simter.domain.member.repository.MemberRepository;
import com.simter.domain.member.service.KakaoOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;
    private final MemberRepository memberRepository;

    @PostMapping("/api/v1/login/kakao")
    public ApiResponse<SocialLoginDto> login(@RequestParam("code") String code)
        throws IOException {
        JsonNode res = kakaoOAuthService.getAccessToken(code);
        String accessToken = res.get("access_token").toString();
        String refreshToken = res.get("refresh_token").toString();

        JwtTokenDto token = JwtTokenDto.builder()
            .grantType("Bearer")
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();

        String email = kakaoOAuthService.getEmail(accessToken);

        SocialLoginDto response = SocialLoginDto.builder()
            .loginType("kakao")
            .token(token)
            .email(email)
            .isMember(memberRepository.existsByEmail(email))
            .build();
        return ApiResponse.onSuccess(response);

    }
}
