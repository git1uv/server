package com.simter.domain.calendar.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.calendar.converter.CalendarsConverter;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDayDto;
import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.calendar.repository.CalendarsRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CalendarsService {

    private final CalendarsRepository calendarsRepository;
    private final MemberRepository memberRepository;

    //월별 달력 조회
    public List<CalendarsHomeDayDto> getMonthlyCalendar(String email, int year, int month) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        LocalDate startDate = LocalDate.of(year, month, 1);

        LocalDate endDate = LocalDate.of(year, month, 1);

        List<Calendars> calendarsList = calendarsRepository.findByUserIdAndDateBetween(member, startDate, endDate);

        List<CalendarsHomeDayDto> calendarsResponse = new ArrayList<>();

        for (int i = 0; i < calendarsList.size() - 1; i++) {
            Calendars calendar = calendarsList.get(i);
            calendarsResponse.add(CalendarsConverter.convertToMonthlyCalendar(calendar));
        }
        return calendarsResponse;
    }

}
