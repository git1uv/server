package com.simter.domain.member.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.mail.repository.MailRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException(email));

        return Member.builder()
            .id(member.getId())
            .password(member.getPassword())
            .nickname(member.getNickname())
            .hasAirplane(member.isHasAirplane())
            .chatbot(member.getChatbot())
            .email(member.getEmail())
            .loginType(member.getLoginType())
            .status(member.isStatus())
            .inactiveDate(member.getInactiveDate())
            .build();
    }
}
