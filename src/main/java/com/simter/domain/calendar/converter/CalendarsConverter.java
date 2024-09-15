package com.simter.domain.calendar.converter;

import com.simter.domain.calendar.dto.CalendarsResponseDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayCounselingLogDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDaySolutionDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDayDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.NewCalendarsDto;
import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.entity.Solution;
import com.simter.domain.chatbot.repository.CalendarCounselingLogRepository;
import com.simter.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalendarsConverter {

    private final CalendarCounselingLogRepository calendarCounselingLogRepository;

    public CalendarsHomeDayDto convertToMonthlyCalendar(Member member, Calendars calendars) {
        Optional<CounselingLog> counselingLog = Optional.ofNullable(
            calendarCounselingLogRepository.findByUserAndCalendarsDate(member, calendars.getDate()).get()
                .getCounselingLog());
        if (counselingLog.isPresent()) {
            return CalendarsHomeDayDto.builder()
                .calendarId(calendars.getId())
                .date(calendars.getDate())
                .emotion(calendars.getEmotion())
                .hasCounseling(true)
                .chatbotType(counselingLog.get().getChatbotType())
                .build();
        } else {
            return CalendarsHomeDayDto.builder()
                .calendarId(calendars.getId())
                .date(calendars.getDate())
                .emotion(calendars.getEmotion())
                .hasCounseling(false)
                .chatbotType("none")
                .build();
        }
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

    public static Calendars convertToEntity(NewCalendarsDto newCalendarsDto) {
        return Calendars.builder()
            .userId(newCalendarsDto.getUserId())
            .date(newCalendarsDto.getDate())
            .build();
    }

    public static CalendarsDayDto convertToDailyCalendar(Optional<Calendars> calendar, LocalDate date, List<CalendarsDaySolutionDto> solutions,
    List<CalendarsDayCounselingLogDto> counselingLog) {
        return CalendarsDayDto.builder()
            .calendarId(calendar.get().getId())
            .diary(calendar.get().getDiary())
            .emotion(calendar.get().getEmotion())
            .date(date)
            .counselingLog(counselingLog)
            .solution(solutions)
            .build();
    }
}
