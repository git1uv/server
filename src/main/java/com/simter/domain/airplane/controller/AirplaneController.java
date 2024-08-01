package com.simter.domain.airplane.controller;

import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.airplane.dto.AirplanePostRequestDto;
import com.simter.domain.airplane.dto.AirplanePostResponseDto;
import com.simter.domain.airplane.exception.AirplaneGetException;
import com.simter.domain.airplane.exception.AirplanePostException;
import com.simter.domain.airplane.service.AirplaneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AirplaneController {

    private final AirplaneService airplaneService;

    // 종이비행기 작성 API (POST)
    @PostMapping("/airplane")
    public ResponseEntity<AirplanePostResponseDto> sendAirplane(@RequestBody AirplanePostRequestDto requestDto) {
        try {
            AirplanePostResponseDto response = airplaneService.sendAirplane(requestDto);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            throw new AirplanePostException("종이 비행기 보내기 실패");
        }
    }

    // 종이비행기 조회 API (GET)
    @GetMapping("/airplane/{receiverId}")
    public ResponseEntity<AirplaneGetResponseDto> getAirplane(@PathVariable Long receiverId) {
        try {
            AirplaneGetResponseDto response = airplaneService.getAirplane(receiverId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new AirplaneGetException("종이 비행기 불러오기 실패");
        }
    }
}
