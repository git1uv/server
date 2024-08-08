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
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입
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

    //로그인
    @Transactional
    public LoginResponseDto login(String email, String password) {
        Optional<Member> user = memberRepository.findByEmail(email);
        if (user.isPresent()) {
            if (!encoder.matches(password, user.get().getPassword())) {
                throw new ErrorHandler(ErrorStatus.INVALID_LOGIN);
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
            throw new ErrorHandler(ErrorStatus.INVALID_LOGIN);
        }
    }

    //로그아웃
    public void logout(String token) {
        String email = jwtTokenProvider.getEmail(token);
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            Member user = member.get();
            user.setRefreshToken(null);
            memberRepository.save(user);
        } else {
            throw new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }
    }

    //닉네임, 비밀번호, 이메일 유효 검증
    public void validateRegister(String email, String password, String nickname) {
        validateEmail(email);
        validatePassword(password);
        validateNickname(nickname);
    }

    //이메일 중복 조회
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

    //이메일 형식 확인
    public void validateEmail(String email) {
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", email)) {
            throw new ErrorHandler(ErrorStatus.INVALID_EMAIL_FORMAT);
        }
    }

    //비밀번호 형식 확인
    public void validatePassword(String password) {
        if (!Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$", password)) {
            throw new ErrorHandler(ErrorStatus.INVALID_PASSWORD_FORMAT);
        }
    }

    //닉네임 형식 확인
    public void validateNickname(String nickname) {
        if (!Pattern.matches("^[가-힣a-zA-Z]{1,10}$", nickname)) {
            throw new ErrorHandler(ErrorStatus.INVALID_NICKNAME_FORMAT);
        }
    }


}