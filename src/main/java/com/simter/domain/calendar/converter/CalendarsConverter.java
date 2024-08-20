package com.simter.domain.calendar.converter;

import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDayDto;
import com.simter.domain.calendar.entity.Calendars;
import org.springframework.stereotype.Component;

@Component
public class CalendarsConverter {
    public static CalendarsHomeDayDto convertToMonthlyCalendar(Calendars calendars, Long month) {

        return CalendarsHomeDayDto.builder()
            .calendarId(calendars.getId())
            .date(calendars.getDate())
            .emotion(calendars.getEmotion())
            .hasCounseling(calendars.get)
    }
}
