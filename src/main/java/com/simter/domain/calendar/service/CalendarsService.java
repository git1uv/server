package com.simter.domain.calendar.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.calendar.converter.CalendarsConverter;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDayDto;
import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.calendar.repository.CalendarsRepository;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CalendarsService {

    private final CalendarsRepository calendarsRepository;
    private final MemberRepository memberRepository;
    private final CounselingLogRepository counselingLogRepository;

    //월별 달력 조회
    public List<CalendarsHomeDayDto> getMonthlyCalendar(String email, int year, int month) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Long memberId = member.getId();

        LocalDate startDate = LocalDate.of(year, month, 1);

        LocalDate endDate = LocalDate.of(year, month % 12 + 1, 1);

        List<Calendars> calendarsList = calendarsRepository.findByUserIdAndDateBetween(memberId, startDate, endDate);

        List<CalendarsHomeDayDto> calendarsResponse = new ArrayList<>();

        for (int i = 0; i < ChronoUnit.DAYS.between(startDate, endDate); i++) {
            if (i + 1 != calendarsList.getFirst().getDate().getDayOfMonth()) {

            }
            else {
                Calendars calendar = calendarsList.getFirst();
                calendarsResponse.add(CalendarsConverter.convertToMonthlyCalendar(memberId, calendar));
                calendarsList.removeFirst();
            }
        }
        return calendarsResponse;
    }

    //일별 달력 조회
    public CalendarsDayDto getDailyCalendar(String email, int year, int month, int day) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Long memberId = member.getId();
        LocalDate date = LocalDate.of(year, month, day);

        Optional<Calendars> calendar = calendarsRepository.findByUserIdAndDate(memberId, date);
        if (calendar.isPresent()) {
            return CalendarsDayDto.builder()
                .calendarId(calendar.get().getId())
                .diary(calendar.get().getDiary())
                .emotion(calendar.get().getEmotion())
                .date(date)
                .counselingLog()
                .solution()
        }

    }

}
