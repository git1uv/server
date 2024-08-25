package com.simter.domain.calendar.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.calendar.converter.CalendarsConverter;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayCounselingLogDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDaySolutionDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDayDto;
import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.calendar.repository.CalendarsRepository;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import com.simter.domain.solution.entity.Solution;
import com.simter.domain.solution.repository.SolutionRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.Error;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CalendarsService {

    private final CalendarsRepository calendarsRepository;
    private final MemberRepository memberRepository;
    private final CounselingLogRepository counselingLogRepository;
    private final SolutionRepository solutionRepository;

    //월별 달력 조회
    public List<CalendarsHomeDayDto> getMonthlyCalendar(String email, int year, int month) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        LocalDate startDate = LocalDate.of(year, month, 1);

        LocalDate endDate = LocalDate.of(year, month % 12 + 1, 1);

        List<Calendars> calendarsList = calendarsRepository.findByUserIdAndDateBetween(member, startDate, endDate);

        List<CalendarsHomeDayDto> calendarsResponse = new ArrayList<>();

        for (int i = 0; i < calendarsList.size() - 1; i++) {
            Calendars calendar = calendarsList.get(i);
            calendarsResponse.add(CalendarsConverter.convertToMonthlyCalendar(member, calendar));
        }
        return calendarsResponse;
    }

    //일별 달력 조회
    public CalendarsDayDto getDailyCalendar(String email, int year, int month, int day) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        LocalDate date = LocalDate.of(year, month, day);

        Optional<Calendars> calendar = calendarsRepository.findByUserIdAndDate(member, date);
        if (calendar.isPresent()) {
            List<CounselingLog> counselingLogs = counselingLogRepository.findByUserIdAndDate(member, date);
            List<CalendarsDaySolutionDto> solutionsResponse = new ArrayList<>();
            List<CalendarsDayCounselingLogDto> counselingLogsResponse = new ArrayList<>();
            for (CounselingLog log : counselingLogs) {
                counselingLogsResponse.add(CalendarsConverter.convertCounselingLogToDailyCalendar(log));
                List<Solution> solutions = solutionRepository.findByCounselingLog(log);
                for (Solution solution : solutions) {
                    solutionsResponse.add(CalendarsConverter.convertSolutionToDailyCalendar(solution));
                }
            }
            return CalendarsDayDto.builder()
                .calendarId(calendar.get().getId())
                .diary(calendar.get().getDiary())
                .emotion(calendar.get().getEmotion())
                .date(date)
                .counselingLog(counselingLogsResponse)
                .solution(solutionsResponse)
                .build();
        } else {
            throw new ErrorHandler(ErrorStatus.DAILY_CALENDAR_NOT_FOUND);
        }

    }

}
