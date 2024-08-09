package com.simter.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.simter.domain.member.dto.JwtTokenDto;
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
    KakaoOAuthService kakaoOAuthService;
    MemberRepository memberRepository;
    @PostMapping("/api/v1/login/kakao")
    public void login(@RequestParam("code") String code, HttpServletResponse response)
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
        String redirectUrl;

        if (memberRepository.existsByEmail(email)) {
            redirectUrl = "/api/v1/main";
        } else {
            redirectUrl = UriComponentsBuilder.fromUriString("/api/v1/signup/nickname")
                .queryParam("token", token)
                .queryParam("email", email)
                .queryParam("loginType", "kakao")
                .build().toUriString();
        }
        response.sendRedirect(redirectUrl);

    }
}
