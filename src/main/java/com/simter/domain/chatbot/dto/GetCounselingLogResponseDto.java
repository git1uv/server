package com.simter.domain.chatbot.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCounselingLogResponseDto {

    private Long counselingId;
    private String chatbotType;
    private String title;
    private String summary;
    private String suggestion;
    private List<SolutionDto> solutions;

    @Builder
    @Getter
    public static class SolutionDto {
        private Long solutionId;
        private String content;
        private boolean isCompleted;

    }
}
