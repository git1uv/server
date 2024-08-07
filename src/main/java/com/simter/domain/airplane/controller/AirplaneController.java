package com.simter.domain.airplane.controller;

import com.simter.apiPayload.ApiResponse;
import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.airplane.dto.AirplanePostRequestDto;
import com.simter.domain.airplane.dto.AirplanePostResponseDto;

import com.simter.domain.airplane.service.AirplaneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "종이비행기 API", description = "종이비행기를 작성하고 조회하는 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AirplaneController {

    private final AirplaneService airplaneService;
    private static final Logger logger = LoggerFactory.getLogger(AirplaneController.class);

    // 종이비행기 작성 API (POST)
    @Operation(summary = "종이비행기 작성 API", description = "종이비행기를 작성하는 API")
    @PostMapping("/airplane")
    public ApiResponse<AirplanePostResponseDto> sendAirplane(@RequestBody AirplanePostRequestDto requestDto) {
        airplaneService.sendAirplane(requestDto);
        return ApiResponse.onSuccess(null);
    }

    // 종이비행기 조회 API (GET)
    @Operation(summary = "종이비행기 조회 API", description = "종이비행기를 조회하는 API")
    @GetMapping("/airplane/{receiverId}")
    public ApiResponse<AirplaneGetResponseDto> getAirplane(@PathVariable Long receiverId) {

        AirplaneGetResponseDto response = airplaneService.getAirplane(receiverId);
        return ApiResponse.onSuccess(response);
    }
}
