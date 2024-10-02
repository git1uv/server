package com.simter.config;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.member.dto.JwtTokenDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        JwtTokenDto jwtTokenDto = jwtTokenProvider.resolveToken(request);
        if (token != null && token.startsWith("Bearer ")) {
            if (!jwtTokenProvider.validateToken(jwtTokenDto.getAccessToken())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 토큰입니다.");
                return;
            }
            Authentication authentication = jwtTokenProvider.getAuthentication(jwtTokenDto.getAccessToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
