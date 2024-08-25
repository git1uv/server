package com.simter.domain.calendar.controller;

import com.simter.apiPayload.ApiResponse;
import com.simter.config.JwtTokenProvider;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsDayDto;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDayDto;
import com.simter.domain.calendar.service.CalendarsService;
import com.simter.domain.member.dto.JwtTokenDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CalendarsController {

    JwtTokenProvider jwtTokenProvider;
    CalendarsService calendarsService;

    @Operation(summary = "달력 홈 API", description = "월별로 달력을 가지고 오는 API")
    @GetMapping("/api/v1/calendar/{year}/{month}/home")
    public ApiResponse<List<CalendarsHomeDayDto>> calendarsHome(HttpServletRequest request,
        @PathVariable int year, @PathVariable int month) {
        JwtTokenDto token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getEmail(token.getAccessToken());
        return ApiResponse.onSuccess(calendarsService.getMonthlyCalendar(email, year, month));
    }

    @Operation(summary = "달력 일별 조회 API", description = "특정 날짜에 해당하는 기록을 가지고 오는 API")
    @GetMapping("/api/v1/calendar/today/{year}/{month}/{day}")
    public ApiResponse<CalendarsDayDto> calendarsDay(HttpServletRequest request,
        @PathVariable int year, @PathVariable int month, @PathVariable int day) {
        JwtTokenDto token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getEmail(token.getAccessToken());
        return ApiResponse.onSuccess(calendarsService.getDailyCalendar(email, year, month, day));
    }

    @Operation(summary = "한줄 일기 저장 API", description = "오늘의 한줄일기를 작성하고 저장하는 api")
    @PatchMapping("/api/v1/calendar/today/{calendarId}/diary")
    public ApiResponse<Void> updateDiary(HttpServletRequest request, @PathVariable Long calendarId,
        @RequestBody String content) {
        JwtTokenDto token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getEmail(token.getAccessToken());
        calendarsService.updateDiary(email, calendarId, content);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "해결책 완료 여부 변경 API", description = "오늘의 해결책 1개를 완료/미완료 시키는 api")
    @PatchMapping("/api/v1/calendar/today/solution/{solutionId}/done")
    public ApiResponse<Void> updateSolution(HttpServletRequest request, @PathVariable Long solutionId,
        @RequestBody boolean isCompleted) {
        JwtTokenDto token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getEmail(token.getAccessToken());
        calendarsService.updateSolution(email, solutionId, isCompleted);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "해결책 삭제 API", description = "오늘의 해결책 1개를 삭제시키는 api")
    @DeleteMapping("/api/v1/calendar/today/solution/{solutionId}/delete")
    public ApiResponse<Void> deleteSolution(HttpServletRequest request, @PathVariable Long solutionId) {
        JwtTokenDto token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getEmail(token.getAccessToken());
        calendarsService.deleteSolution(email, solutionId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "감정 기록 API", description = "오늘의 감정을 기록하는 api")
    @PatchMapping("/api/v1/calendar/today/:calendarId/emotion")
    public ApiResponse<Void> updateEmotion(HttpServletRequest request, @PathVariable Long calendarId,
        @RequestBody String emotion) {
        JwtTokenDto token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getEmail(token.getAccessToken());
        calendarsService.updateEmotion(email, calendarId, emotion);
        return ApiResponse.onSuccess(null);
    }
}
