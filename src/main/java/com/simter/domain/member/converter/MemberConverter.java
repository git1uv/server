package com.simter.domain.member.converter;

import com.simter.domain.member.dto.MemberRequestDto.RegisterRequestDto;
import com.simter.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {

    public static Member convertToEntity(RegisterRequestDto registerRequestDto) {
        return Member.builder()
                .email(registerRequestDto.getEmail())
                .password(registerRequestDto.getPassword())
                .nickname(registerRequestDto.getNickname())
                .loginType("general")
                .build();
    }
}
