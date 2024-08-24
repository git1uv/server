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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CalendarsController {

    JwtTokenProvider jwtTokenProvider;
    CalendarsService calendarsService;

    @Operation(summary = "달력 홈 API", description = "월별로 달력을 가지고 오는 API")
    @GetMapping("/api/v1/calendar/:year/:month/home")
    public ApiResponse<List<CalendarsHomeDayDto>> calendarsHome(HttpServletRequest request,
        @RequestParam int year, @RequestParam int month) {
        JwtTokenDto token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getEmail(token.getAccessToken());
        return ApiResponse.onSuccess(calendarsService.getMonthlyCalendar(email, year, month));
    }

    @Operation(summary = "달력 일별 조회 API", description = "특정 날짜에 해당하는 기록을 가지고 오는 API")
    @GetMapping("/api/v1/calendar/today/:year/:month/:day")
    public ApiResponse<CalendarsDayDto> calendarsDay(HttpServletRequest request,
        @RequestParam int year, @RequestParam int month, @RequestParam int day) {
        JwtTokenDto token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getEmail(token.getAccessToken());
        return ApiResponse.onSuccess(calendarsService.getDailyCalendar(email, year, month, day));
    }

}
