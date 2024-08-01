package com.simter;

import com.simter.domain.airplane.exception.AirplaneGetException;
import com.simter.domain.airplane.exception.AirplanePostException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ExceptionHandler(AirplaneGetException.class)
    public ResponseEntity<String> handleAirplaneGetException() {
        return ResponseEntity.status(500).body("종이비행기 조회 중 오류가 발생하였습니다!");
    }

    @ExceptionHandler(AirplanePostException.class)
    public ResponseEntity<String> handleAirplanePostException() {
        return ResponseEntity.status(500).body("종이비행기 전송 중 오류가 발생하였습니다!");
    }

}
