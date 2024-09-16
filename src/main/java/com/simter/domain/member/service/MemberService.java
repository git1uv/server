package com.simter.domain.member.service;

import static org.springframework.security.core.context.SecurityContextHolder.setContext;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.config.JwtTokenProvider;
import com.simter.domain.member.converter.MemberConverter;
import com.simter.domain.member.dto.JwtTokenDto;
import com.simter.domain.member.dto.MainDto;
import com.simter.domain.member.dto.MemberRequestDto.PasswordChangeDto;
import com.simter.domain.member.dto.MemberRequestDto.RegisterDto;
import com.simter.domain.member.dto.MemberResponseDto.EmailValidationResponseDto;
import com.simter.domain.member.dto.MemberResponseDto.LoginResponseDto;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;

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
        if (!memberRepository.existsByEmail(email)) {
            memberRepository.save(member);
        }
    }

    //로그인
    @Transactional
    public LoginResponseDto login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.INVALID_LOGIN));
        if (!encoder.matches(password, member.getPassword())) {
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

    }

    //로그아웃
    public void logout(String token) {
        String email = jwtTokenProvider.getEmail(token);
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        member.setRefreshToken(null);
        memberRepository.save(member);
    }

    //비밀번호 재발송
    public void tempPw(String email) throws MessagingException {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MAIL_NOT_REGISTERED));
        Random random = new Random();
        String newPassword = RandomStringUtils.randomAlphanumeric(8 + random.nextInt(9));

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("[심터] 비밀번호 재설정 메일입니다.");
        mimeMessageHelper.setText("새 비밀번호: " + newPassword, true);
        mailSender.send(mimeMessage);

        String encryptedPassword = encoder.encode(newPassword);
        member.setPassword(encryptedPassword);
        memberRepository.save(member);
    }

    //메인화면 api

    public MainDto main(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        MainDto mainDto = MainDto.builder()
            .mailAlert(member.isMailAlert())
            .nickname(member.getNickname())
            .airplane(member.isHasAirplane())
            .build();
        member.setMailAlert(false);
        return mainDto;
    }

    //닉네임 변경
    public void changeNickname(String email, String nickname) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        validateNickname(nickname);
        member.setNickname(nickname);
        memberRepository.save(member);
    }

    //비밀번호 변경
    public void changePassword(String email, PasswordChangeDto passwordChangeDto) {
        String oldPw = passwordChangeDto.getOldPassword();
        String newPw = passwordChangeDto.getNewPassword();
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        if (!encoder.matches(oldPw, member.getPassword())) {
            throw new ErrorHandler(ErrorStatus.WRONG_PASSWORD);
        }
        validatePassword(newPw);
        member.setPassword(encoder.encode(newPw));
        memberRepository.save(member);
    }

    //회원 탈퇴
    public void deleteAccount(String email) {
        LocalDateTime dateTime = LocalDateTime.now();
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        member.changeStatusToInactive();;
        member.setInactiveDate(dateTime);
        memberRepository.save(member);
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