package com.simter.domain.calendar.dto;

import java.time.LocalDate;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CalendarsResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CalendarsHomeDayDto {
        private Long calendarId;
        private LocalDate date;
        private String emotion;
        private Boolean hasCounseling;
    }
}
