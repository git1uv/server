package com.simter.domain.chatbot.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaudeResponseDto {
    Long counselingLogId;
    String redFlag;
    String emotion;
    String message;
    String createdAt;
}
