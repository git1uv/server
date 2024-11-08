package com.simter.domain.calendar.converter;

import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayCounselingLogDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDaySolutionDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDayDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.NewCalendarsDto;
import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.chatbot.dto.CounselingResponseDto.CounselingDto;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.entity.Solution;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import com.simter.domain.member.entity.Member;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalendarsConverter {

    private final CounselingLogRepository counselingLogRepository;

    public CalendarsHomeDayDto convertToMonthlyCalendar(Member member, Calendars calendars) {
        List<CounselingLog> counselingLogs = counselingLogRepository.findByCalendars(calendars);
        if (!counselingLogs.isEmpty()) {
            return CalendarsHomeDayDto.builder()
                .calendarId(calendars.getId())
                .date(calendars.getDate())
                .emotion(calendars.getEmotion())
                .hasCounseling(true)
                .chatbotType(counselingLogs.getFirst().getChatbotType())
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

    public static Calendars solutionToCalendar(CounselingLog counselingLog){
        LocalDate koreanDate = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate();
        return Calendars.builder()
            .userId(counselingLog.getUser())
            .date(koreanDate)
            .emotion("none")
            .diary("")
            .build();
    }

}
