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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        JwtTokenDto jwtTokenDto = jwtTokenProvider.resolveToken(request);

        System.out.println(jwtTokenDto.getAccessToken());
        System.out.println(isTokenBlacklisted(jwtTokenDto.getAccessToken()));
        if (token != null && token.startsWith("Bearer ")) {
            if (isTokenBlacklisted(jwtTokenDto.getAccessToken())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("로그아웃 처리된 엑세스 토큰입니다.");
                return;
            }
            if (!jwtTokenProvider.validateToken(jwtTokenDto.getAccessToken())) {
                throw new ErrorHandler(ErrorStatus.JWT_UNSUPPORTED_TOKEN);
            }
            Authentication authentication = jwtTokenProvider.getAuthentication(jwtTokenDto.getAccessToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isTokenBlacklisted(String accessToken) {
        String blackToken = (String) redisTemplate.opsForValue().get(accessToken);
        return StringUtils.hasText(blackToken);
    }
}
