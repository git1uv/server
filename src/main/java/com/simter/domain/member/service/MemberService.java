package com.simter.domain.member.service;

import com.simter.domain.member.converter.MemberConverter;
import com.simter.domain.member.dto.MemberRequestDto.RegisterRequestDto;
import com.simter.domain.member.dto.MemberResponseDto.EmailValidationResponseDto;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.exception.InvalidEmailFormatException;
import com.simter.domain.member.exception.InvalidNicknameFormatException;
import com.simter.domain.member.exception.InvalidPasswordFormatException;
import com.simter.domain.member.repository.MemberRepository;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

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