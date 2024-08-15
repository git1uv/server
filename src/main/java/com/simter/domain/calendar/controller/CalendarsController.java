package com.simter.domain.calendar.controller;

import com.simter.apiPayload.ApiResponse;
import com.simter.config.JwtTokenProvider;
import com.simter.domain.calendar.dto.CalendarsResponseDto.CalendarsHomeDto;
import com.simter.domain.calendar.service.CalendarsService;
import com.simter.domain.member.dto.JwtTokenDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
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
    @GetMapping("/api/v1/calendar/:month/home")
    public ApiResponse<CalendarsHomeDto> calendarsHome(HttpServletRequest request, @RequestParam String month) {
        JwtTokenDto token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getEmail(token.getAccessToken());
        return ApiResponse.onSuccess();
    }

}
