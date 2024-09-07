package com.simter.domain.calendar.converter;

import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayCounselingLogDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDaySolutionDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDayDto;
import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.repository.CalendarCounselingLogRepository;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.solution.entity.Solution;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalendarsConverter {

    private final CalendarCounselingLogRepository calendarCounselingLogRepository;

    public CalendarsHomeDayDto convertToMonthlyCalendar(Member member, Calendars calendars) {

        return CalendarsHomeDayDto.builder()
            .calendarId(calendars.getId())
            .date(calendars.getDate())
            .emotion(calendars.getEmotion())
            .hasCounseling(calendarCounselingLogRepository.existsByUserAndCalendarsDate(member, calendars.getDate()))
            .build();
    }

    public static CalendarsDayCounselingLogDto convertCounselingLogToDailyCalendar(CounselingLog counselingLog) {
        return CalendarsDayCounselingLogDto.builder()
            .id(counselingLog.getId())
            .title(counselingLog.getTitle())
            .time(counselingLog.getStartedAt())
            .chatbotType(counselingLog.getChatbotType())
            .build();
    }

    public static CalendarsDaySolutionDto convertSolutionToDailyCalendar(Solution solution) {
        return CalendarsDaySolutionDto.builder()
            .id(solution.getId())
            .content(solution.getContent())
            .isCompleted(solution.getIsCompleted())
            .build();
    }
}
