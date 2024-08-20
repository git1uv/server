package com.simter.domain.calendar.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDto;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.ErrorState;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CalendarsService {

    MemberRepository memberRepository;

    //월별 달력 조회
    public CalendarsHomeDto getMonthlyCalendar(String email, Long year, Long month) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Long memberId = member.getId();
    }

}
