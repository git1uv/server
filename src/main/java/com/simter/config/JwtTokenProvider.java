package com.simter.config;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.member.dto.JwtTokenDto;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final MemberRepository memberRepository;
    private static final String AUTHORITIES_KEY = "ROLE_USER";

    public JwtTokenDto generateToken(Authentication authentication, String email) {

        long currentTime = (new Date()).getTime();

        Date accessTokenExpirationTime = new Date(currentTime + (1000 * 60 * 60 * 3));
        Date refreshTokenExpirationTime = new Date(currentTime + (1000 * 60 * 60 * 24 * 7));

        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put(AUTHORITIES_KEY, authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        claims.put("email", email);

        String accessToken = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(currentTime))
            .setExpiration(accessTokenExpirationTime)
            .signWith(secretKey)
            .compact();

        String refreshToken = Jwts.builder()
            .setExpiration(refreshTokenExpirationTime)
            .signWith(secretKey)
            .compact();

        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        return JwtTokenDto.builder()
            .grantType("Bearer")
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();

    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(accessToken)
            .getBody();

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    //액세스 토큰과 리프레시 토큰 함께 재발행
    public JwtTokenDto reissueToken(String email, String refreshToken) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String storedRefreshToken = member.getRefreshToken();

        if (!storedRefreshToken.equals(refreshToken)) {
            throw new ErrorHandler(ErrorStatus.JWT_TOKEN_NOT_FOUND);
        }

        Authentication authentication = getAuthentication(storedRefreshToken);

        return generateToken(authentication, email);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {

            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {

            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {

            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {

            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    public String getEmail(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }
}
