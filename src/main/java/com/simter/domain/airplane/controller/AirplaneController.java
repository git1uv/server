package com.simter.domain.airplane.controller;

import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.airplane.dto.AirplanePostRequestDto;
import com.simter.domain.airplane.dto.AirplanePostResponseDto;
import com.simter.domain.airplane.exception.AirplaneGetException;
import com.simter.domain.airplane.exception.AirplanePostException;
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
    public ResponseEntity<AirplanePostResponseDto> sendAirplane(@RequestBody AirplanePostRequestDto requestDto) {
        try {
            AirplanePostResponseDto response = airplaneService.sendAirplane(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AirplanePostException e) {
            logger.error("Error sending airplane: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AirplanePostResponseDto("종이 비행기 보내기 실패"));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AirplanePostResponseDto("종이 비행기 보내기 실패"));
        }
    }

    // 종이비행기 조회 API (GET)
    @GetMapping("/airplane/{receiverId}")
    public ResponseEntity<AirplaneGetResponseDto> getAirplane(@PathVariable Long receiverId) {
        try {
            AirplaneGetResponseDto response = airplaneService.getAirplane(receiverId);
            return ResponseEntity.ok(response);
        } catch (AirplaneGetException e) {
            logger.error("Error getting airplane: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AirplaneGetResponseDto("종이 비행기 불러오기 실패", null));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AirplaneGetResponseDto("종이 비행기 불러오기 실패", null));
        }
    }
}