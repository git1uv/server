package com.simter.domain.airplane.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AirplaneGetResponseDto {
    private String message;
    private DataDto data;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataDto {
        private String writerName;
        private String content;
        private String createdAt;
    }

}
