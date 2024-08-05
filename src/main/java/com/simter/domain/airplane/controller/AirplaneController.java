package com.simter.domain.airplane.controller;

import com.simter.apiPayload.ApiResponse;
import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.airplane.dto.AirplanePostRequestDto;
import com.simter.domain.airplane.dto.AirplanePostResponseDto;

import com.simter.domain.airplane.service.AirplaneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AirplaneController {

    private final AirplaneService airplaneService;
    private static final Logger logger = LoggerFactory.getLogger(AirplaneController.class);

    // 종이비행기 작성 API (POST)
    @PostMapping("/airplane")
    public ResponseEntity<ApiResponse<AirplanePostResponseDto>> sendAirplane(@RequestBody AirplanePostRequestDto requestDto) {
            AirplanePostResponseDto response = airplaneService.sendAirplane(requestDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    // 종이비행기 조회 API (GET)
    @GetMapping("/airplane/{receiverId}")
    public ResponseEntity<ApiResponse<AirplaneGetResponseDto>> getAirplane(@PathVariable Long receiverId) {

        AirplaneGetResponseDto response = airplaneService.getAirplane(receiverId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
