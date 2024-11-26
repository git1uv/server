package com.simter.domain.member.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.config.JwtTokenProvider;
import com.simter.domain.member.converter.MemberConverter;
import com.simter.domain.member.dto.JwtTokenDto;
import com.simter.domain.member.dto.MainDto;
import com.simter.domain.member.dto.MemberRequestDto.PasswordChangeDto;
import com.simter.domain.member.dto.MemberRequestDto.RegisterDto;
import com.simter.domain.member.dto.MemberRequestDto.SocialRegisterDto;
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
        } else {
            Member orgMember = memberRepository.findByEmail(email).orElseThrow();
            registerOrgMember(orgMember, newRegisterDto, "general");
        }
    }

    //소셜 회원가입
    public void register(SocialRegisterDto socialRegisterDto) {
        String email = socialRegisterDto.getEmail();
        String nickname = socialRegisterDto.getNickname();
        String loginType = socialRegisterDto.getLoginType();
        JwtTokenDto token = socialRegisterDto.getToken();
        RegisterDto newRegisterDto = RegisterDto.builder()
            .email(email)
            .password("")
            .nickname(nickname)
            .loginType(loginType)
            .build();
        Member member = MemberConverter.convertToEntity(newRegisterDto);
        if (!memberRepository.existsByEmail(email)) {
            member.setRefreshToken(token.getRefreshToken());
            memberRepository.save(member);
        } else {
            Member orgMember = memberRepository.findByEmail(email).orElseThrow();
            registerOrgMember(orgMember, newRegisterDto, "social");
        }
    }

    //기존 회원 가입
    public void registerOrgMember(Member member, RegisterDto registerDto, String registerType) {
        member.setNickname(registerDto.getNickname());
        if (registerType.equals("general")) {
            member.setPassword(registerDto.getPassword());
        } else {
            member.setPassword("");
        }
        member.setLoginType(registerDto.getLoginType());
        member.changeStatus();
        member.setInactiveDate(null);
        memberRepository.saveAndFlush(member);
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

        String htmlMsg = "<div style='font-family: Arial, sans-serif; text-align: center; padding: 20px; background-color: #f4f4f9;'>" +
            "  <h2 style='color: #333;'>비밀번호 재설정 안내</h2>" +
            "  <p style='font-size: 16px;'>안녕하세요, " + member.getNickname() + "님!</p>" +
            "  <p style='font-size: 16px;'>임시 비밀번호입니다.</p>" +
            "  <div style='margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 8px; border: 1px solid #ddd; display: inline-block;'>" +
            "    <p style='font-size: 18px; color: #555;'>새 비밀번호: <strong style='font-size: 20px; color: #000;'>" + newPassword + "</strong></p>" +
            "  </div>" +
            "  <p style='font-size: 14px; color: #777;'>로그인 후 비밀번호를 변경하시기 바랍니다.</p>" +
            "  <footer style='margin-top: 30px; font-size: 12px; color: #999;'>문의사항은 <a href='mailto:support@shimter.com'>a64494293@gmail.com</a>으로 연락 주세요.</footer>" +
            "</div>";

        mimeMessageHelper.setText(htmlMsg, true);
        mailSender.send(mimeMessage);

        String encryptedPassword = encoder.encode(newPassword);
        member.setPassword(encryptedPassword);
        memberRepository.save(member);
    }


    //메인화면 api
    public MainDto main(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        //종이비행기
        MainDto mainDto = MainDto.builder()
            .mailAlert(member.isMailAlert())
            .nickname(member.getNickname())
            .airplane(member.isHasAirplane())
            .build();
        return mainDto;
    }

    //새 메일 알림 끄기
    public void turnOffMailAlert(String email, String mailAlert){
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        member.setMailAlert(Boolean.parseBoolean(mailAlert));
    }

    //닉네임 변경
    public void changeNickname(String email, String nickname) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
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
        member.setPassword(encoder.encode(newPw));
        memberRepository.save(member);
    }

    //회원 탈퇴
    public void deleteAccount(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        member.changeStatus();
        member.setInactiveDate(LocalDateTime.now());
        memberRepository.saveAndFlush(member);
    }

    //이메일 중복 조회
    public EmailValidationResponseDto validateDuplicate(String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if (findMember.isEmpty() || !findMember.get().isStatus()) {
            return EmailValidationResponseDto.builder()
                .isValid(true)
                .build();
        } else {
            return EmailValidationResponseDto.builder()
                .isValid(false)
                .build();
        }
    }
}