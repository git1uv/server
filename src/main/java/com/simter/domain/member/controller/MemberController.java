package com.simter.domain.member.controller;

import com.simter.domain.member.dto.JwtTokenDto;
import com.simter.domain.member.dto.LoginDto;
import com.simter.domain.member.dto.MemberRequestDto.EmailValidationRequestDto;
import com.simter.domain.member.dto.MemberRequestDto.RegisterRequestDto;
import com.simter.domain.member.dto.MemberResponseDto.BasicResponseDto;
import com.simter.domain.member.dto.MemberResponseDto.EmailValidationResponseDto;
import com.simter.domain.member.dto.MemberResponseDto.LoginResponseDto;
import com.simter.domain.member.exception.InvalidEmailFormatException;
import com.simter.domain.member.exception.InvalidNicknameFormatException;
import com.simter.domain.member.exception.InvalidPasswordFormatException;
import com.simter.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/v1/register/general")
    public ResponseEntity<BasicResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        try {
            memberService.register(registerRequestDto);
            BasicResponseDto res = BasicResponseDto.builder()
                    .status(201)
                    .message("회원가입 성공")
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
        catch (InvalidEmailFormatException e) {
            BasicResponseDto res = BasicResponseDto.builder()
                    .status(403)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(403).body(res);
        }
        catch (InvalidPasswordFormatException e) {
            BasicResponseDto res = BasicResponseDto.builder()
                    .status(403)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(403).body(res);
        }
        catch (InvalidNicknameFormatException e) {
            BasicResponseDto res = BasicResponseDto.builder()
                    .status(403)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(403).body(res);
        }
        catch (Exception e) {
            e.printStackTrace();
            BasicResponseDto res = BasicResponseDto.builder()
                    .status(500)
                    .message("회원가입 실패")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @GetMapping("/api/v1/register/general/check")
    public ResponseEntity<EmailValidationResponseDto> checkRegister(@RequestBody EmailValidationRequestDto emailValidationRequestDto) {
        try {
            EmailValidationResponseDto res = memberService.validateDuplicate(
                    emailValidationRequestDto.getEmail());

            return ResponseEntity.status(HttpStatus.OK).body(res);
            }
        catch (Exception e) {
            e.printStackTrace();
            EmailValidationResponseDto res = EmailValidationResponseDto.builder()
                    .status(500)
                    .message("이메일 중복 조회 실패")
                    .isValid(false)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @PostMapping("/api/v1/login/general")
    public ResponseEntity<LoginResponseDto> signIn(@RequestBody LoginDto loginDto) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();
        String token = memberService.login(email, password);
        LoginResponseDto res = LoginResponseDto.builder()
            .status(200)
            .message("로그인 성공")
            .token(token)
            .build();
        return ResponseEntity.status(200).body(res);
    }
}

