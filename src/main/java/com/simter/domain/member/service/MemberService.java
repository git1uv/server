package com.simter.domain.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simter.config.JwtTokenProvider;
import com.simter.domain.member.converter.MemberConverter;
import com.simter.domain.member.dto.JwtTokenDto;
import com.simter.domain.member.dto.MemberRequestDto.RegisterRequestDto;
import com.simter.domain.member.dto.MemberResponseDto.EmailValidationResponseDto;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.exception.InvalidEmailFormatException;
import com.simter.domain.member.exception.InvalidLoginException;
import com.simter.domain.member.exception.InvalidNicknameFormatException;
import com.simter.domain.member.exception.InvalidPasswordFormatException;
import com.simter.domain.member.repository.MemberRepository;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.core.serializer.Serializer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public void register(RegisterRequestDto registerRequestDto) {
        String email = registerRequestDto.getEmail();
        String password = registerRequestDto.getPassword();
        String nickname = registerRequestDto.getNickname();
        validateRegister(email, password, nickname);
        String encryptedPassword = encoder.encode(password);
        RegisterRequestDto newRegisterRequestDto = RegisterRequestDto.builder()
                .email(email)
                .password(encryptedPassword)
                .nickname(nickname)
                .build();
        Member member = MemberConverter.convertToEntity(newRegisterRequestDto);
        memberRepository.save(member);
    }

    @Transactional
    public String login(String email, String password) {
        Optional<Member> user = memberRepository.findByEmail(email);
        if (user.isPresent()) {
            if (!encoder.matches(password, user.get().getPassword())) {
                throw new InvalidLoginException();
            } else {
                UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(email, password);
                Authentication authentication
                    = authenticationManagerBuilder.getObject().authenticate(token);

                JwtTokenDto jwtToken =  jwtTokenProvider.generateToken(authentication,email);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return objectMapper.writeValueAsString(jwtToken);
                } catch (Exception e) {
                    throw new RuntimeException("토큰 JSON 변환 중 오류", e);
                }
            }
        } else {
            throw new InvalidLoginException();
        }
    }

    public void validateRegister(String email, String password, String nickname){
        validateEmail(email);
        validatePassword(password);
        validateNickname(nickname);
    }

    public EmailValidationResponseDto validateDuplicate(String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if (!findMember.isPresent()) {
            return EmailValidationResponseDto.builder()
                    .status(200)
                    .message("사용할 수 있는 이메일입니다.")
                    .isValid(true)
                    .build();
        }
        else{
            return EmailValidationResponseDto.builder()
                    .status(200)
                    .message("사용할 수 없는 이메일입니다.")
                    .isValid(false)
                    .build();
        }
    }

    public void validateEmail(String email) {
        if(!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", email)){
            throw new InvalidEmailFormatException();
        }
    }

    public void validatePassword(String password) {
        if(!Pattern.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,16}$", password)){
            throw new InvalidPasswordFormatException();
        }
    }

    public void validateNickname(String nickname) {
        if(!Pattern.matches("^[가-힣a-zA-Z]{1,10}$", nickname)){
            throw new InvalidNicknameFormatException();
        }
    }


}