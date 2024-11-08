package com.simter.domain.chatbot.dto;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class CounselingResponseDto {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselingDto {
        private Long counselingLogId;
        private Long calendarId;
        private String chatbotType;
        private String title;
        private String summary;
        private String suggestion;
        private List<Solution> solutions;
        private String endedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Solution {
        private Long solutionId;
        private String content;
    }

}

