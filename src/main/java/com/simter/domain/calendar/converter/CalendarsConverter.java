package com.simter.domain.calendar.converter;

import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDayDto;
import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.chatbot.repository.CalendarCounselingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalendarsConverter {

    public static CalendarCounselingLogRepository counselingLogRepository;

    public static CalendarsHomeDayDto convertToMonthlyCalendar(Calendars calendars) {

        return CalendarsHomeDayDto.builder()
            .calendarId(calendars.getId())
            .date(calendars.getDate())
            .emotion(calendars.getEmotion())
            .hasCounseling(counselingLogRepository.existsByCalendarsDate(calendars.getDate()))
            .build();
    }
}
