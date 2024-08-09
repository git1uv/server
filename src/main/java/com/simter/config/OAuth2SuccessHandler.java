package com.simter.config;

import com.simter.domain.member.dto.JwtTokenDto;
import com.simter.domain.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    JwtTokenProvider jwtTokenProvider;
    MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        JwtTokenDto jwtToken = jwtTokenProvider.generateToken(authentication, email);
        String token = jwtToken.getAccessToken();
        String redirectUrl;
        if (memberRepository.existsByEmail(email)) {
            redirectUrl = "/api/v1/main";
        } else {
            redirectUrl = UriComponentsBuilder.fromUriString("/api/v1/signup/nickname")
                .queryParam("token", token)
                .queryParam("email", email)
                .queryParam("loginType", "google")
                .build().toUriString();
        }
        response.sendRedirect(redirectUrl);
    }
}
