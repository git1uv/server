package com.simter.domain.calendar.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.calendar.converter.CalendarsConverter;
import com.simter.domain.calendar.dto.CalendarsRequestDto.DiaryDto;
import com.simter.domain.calendar.dto.CalendarsRequestDto.EmotionDto;
import com.simter.domain.calendar.dto.CalendarsRequestDto.IsCompletedDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayCounselingLogDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDaySolutionDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDayDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.NewCalendarsDto;
import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.calendar.repository.CalendarsRepository;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.entity.Solution;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import com.simter.domain.chatbot.repository.SolutionRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import java.time.LocalDate;
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
    private final SolutionRepository solutionRepository;
    private final CalendarsConverter calendarsConverter;

    //월별 달력 조회
    public List<CalendarsHomeDayDto> getMonthlyCalendar(String email, int year, int month) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        LocalDate startDate = LocalDate.of(year, month, 1);

        LocalDate endDate = LocalDate.of(year, month % 12 + 1, 1);

        List<Calendars> calendarsList = calendarsRepository.findByUserIdAndDateBetween(member, startDate, endDate);

        List<CalendarsHomeDayDto> calendarsResponse = new ArrayList<>();

        System.out.println(calendarsList.size());

        for (int i = 0; i < calendarsList.size(); i++) {
            Calendars calendar = calendarsList.get(i);
            if (calendar.getDate().getMonthValue() != month % 12 +1) {
                calendarsResponse.add(calendarsConverter.convertToMonthlyCalendar(member, calendar));
            }
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
            List<CounselingLog> counselingLogs = counselingLogRepository.findByCalendars(calendar.get());
            List<CalendarsDaySolutionDto> solutionsResponse = new ArrayList<>();
            List<CalendarsDayCounselingLogDto> counselingLogsResponse = new ArrayList<>();
            for (CounselingLog log : counselingLogs) {
                counselingLogsResponse.add(CalendarsConverter.convertCounselingLogToDailyCalendar(log));
                List<Solution> solutions = solutionRepository.findByCounselingLog(log);
                for (Solution solution : solutions) {
                    solutionsResponse.add(CalendarsConverter.convertSolutionToDailyCalendar(solution));
                }
            }
            return CalendarsConverter.convertToDailyCalendar(calendar, date, solutionsResponse, counselingLogsResponse);
        } else {
            NewCalendarsDto newCalendarsDto = NewCalendarsDto.builder()
                .userId(member)
                .date(date)
                .build();
            Calendars newCalendar = CalendarsConverter.convertToEntity(newCalendarsDto);
            if (!calendarsRepository.existsByUserIdAndDate(member, date)) {
                Calendars savedCalendar = calendarsRepository.save(newCalendar);
                return CalendarsConverter.convertToDailyCalendar(Optional.of(savedCalendar), date, new ArrayList<>(), new ArrayList<>());
            } else {
                throw new ErrorHandler(ErrorStatus.CALENDAR_ALREADY_EXISTS);
            }
        }
    }

    //한줄 일기 업데이트
    public void updateDiary(Long calendarId, DiaryDto content) {
        Calendars calendar = calendarsRepository.findById(calendarId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.CALENDAR_NOT_FOUND));
        calendar.setDiary(content.getContent());
        calendarsRepository.save(calendar);
    }

    //해결책 완료 여부 업데이트
    public void updateSolution(Long solutionId, IsCompletedDto isComp) {
        Solution solution = solutionRepository.findById(solutionId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.SOLUTION_NOT_FOUND));
        solution.setIsCompleted(Boolean.parseBoolean(isComp.getIsCompleted()));
        solutionRepository.save(solution);
    }

    //해결책 삭제
    public void deleteSolution(Long solutionId) {
        Solution solution = solutionRepository.findById(solutionId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.SOLUTION_NOT_FOUND));

        solutionRepository.delete(solution);
    }

    //감정 업데이트
    public void updateEmotion(Long calendarId, EmotionDto emotion) {
        Calendars calendar = calendarsRepository.findById(calendarId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.CALENDAR_NOT_FOUND));

        calendar.setEmotion(emotion.getEmotion());
        calendarsRepository.save(calendar);
    }

}
