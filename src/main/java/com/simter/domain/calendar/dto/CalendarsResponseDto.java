package com.simter.domain.calendar.dto;

import com.simter.domain.member.entity.Member;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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
        private String chatbotType;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CalendarsDayDto {
        private Long calendarId;
        private LocalDate date;
        private String emotion;
        private String diary;
        private List<CalendarsDayCounselingLogDto> counselingLog;
        private List<CalendarsDaySolutionDto> solution;

    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CalendarsDayCounselingLogDto {
        private Long id;
        private String title;
        private String chatbotType;
        private LocalDateTime time;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CalendarsDaySolutionDto {
        private Long id;
        private String content;
        private boolean isCompleted;

    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NewCalendarsDto {
        private Member userId;
        private LocalDate date;
    }

}
