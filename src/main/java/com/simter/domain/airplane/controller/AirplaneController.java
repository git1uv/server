package com.simter.domain.airplane.controller;

import com.simter.apiPayload.ApiResponse;
import com.simter.apiPayload.code.status.SuccessStatus;
import com.simter.config.JwtTokenProvider;
import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.airplane.dto.AirplanePostRequestDto;
import com.simter.domain.airplane.dto.AirplanePostResponseDto;

import com.simter.domain.airplane.service.AirplaneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "종이비행기 API", description = "종이비행기를 작성하고 조회하는 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AirplaneController {

    private final AirplaneService airplaneService;
    private final JwtTokenProvider jwtTokenProvider;

    // 종이비행기 작성 API
    @Operation(summary = "종이비행기 작성 API", description = "종이비행기를 작성하는 API")
    @PostMapping("/airplane")
    public ApiResponse<AirplanePostResponseDto> sendAirplane(
            @RequestBody AirplanePostRequestDto requestDto,
            HttpServletRequest request) {
        String email = jwtTokenProvider.getEmail(jwtTokenProvider.resolveToken(request).getAccessToken());
        airplaneService.sendAirplane(requestDto, email);
        return ApiResponse.onSuccessCustom(SuccessStatus.AIRPLANE_CREATE, null);
    }

    // 종이비행기 조회 API
    @Operation(summary = "종이비행기 조회 API", description = "종이비행기를 조회하는 API")
    @GetMapping("/airplane")
    public ApiResponse<AirplaneGetResponseDto> getAirplane(
            HttpServletRequest request) {
        String email = jwtTokenProvider.getEmail(jwtTokenProvider.resolveToken(request).getAccessToken());
        AirplaneGetResponseDto response = airplaneService.getAirplane(email);
        return ApiResponse.onSuccessCustom(SuccessStatus.AIRPLANE_GET, response);
    }
}
