package com.simter.domain.member.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.config.JwtTokenProvider;
import com.simter.domain.member.converter.MemberConverter;
import com.simter.domain.member.dto.JwtTokenDto;
import com.simter.domain.member.dto.MemberRequestDto.RegisterDto;
import com.simter.domain.member.dto.MemberResponseDto.EmailValidationResponseDto;
import com.simter.domain.member.dto.MemberResponseDto.LoginResponseDto;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public void register(RegisterDto registerRequestDto) {
        String email = registerRequestDto.getEmail();
        String password = registerRequestDto.getPassword();
        String nickname = registerRequestDto.getNickname();
        String loginType = registerRequestDto.getLoginType();
        validateRegister(email, password, nickname);
        String encryptedPassword = encoder.encode(password);
        RegisterDto newRegisterDto = RegisterDto.builder()
            .email(email)
            .password(encryptedPassword)
            .nickname(nickname)
            .loginType(loginType)
            .build();
        Member member = MemberConverter.convertToEntity(newRegisterDto);
        memberRepository.save(member);
    }

    @Transactional
    public LoginResponseDto login(String email, String password) {
        Optional<Member> user = memberRepository.findByEmail(email);
        if (user.isPresent()) {
            if (!encoder.matches(password, user.get().getPassword())) {
                throw new ErrorHandler(ErrorStatus.INVALID_NICKNAME_FORMAT);
            } else {
                UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(email, password);
                Authentication authentication
                    = authenticationManager.authenticate(token);
                JwtTokenDto jwtToken = jwtTokenProvider.generateToken(authentication, email);

                return LoginResponseDto.builder()
                    .token(jwtToken)
                    .build();
            }
        } else {
            throw new ErrorHandler(ErrorStatus.INVALID_NICKNAME_FORMAT);
        }
    }

    public void validateRegister(String email, String password, String nickname) {
        validateEmail(email);
        validatePassword(password);
        validateNickname(nickname);
    }

    public EmailValidationResponseDto validateDuplicate(String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if (findMember.isEmpty()) {
            return EmailValidationResponseDto.builder()
                .isValid(true)
                .build();
        } else {
            return EmailValidationResponseDto.builder()
                .isValid(false)
                .build();
        }
    }

    public void validateEmail(String email) {
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", email)) {
            throw new ErrorHandler(ErrorStatus.INVALID_EMAIL_FORMAT);
        }
    }

    public void validatePassword(String password) {
        if (!Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$", password)) {
            throw new ErrorHandler(ErrorStatus.INVALID_PASSWORD_FORMAT);
        }
    }

    public void validateNickname(String nickname) {
        if (!Pattern.matches("^[가-힣a-zA-Z]{1,10}$", nickname)) {
            throw new ErrorHandler(ErrorStatus.INVALID_NICKNAME_FORMAT);
        }
    }


}