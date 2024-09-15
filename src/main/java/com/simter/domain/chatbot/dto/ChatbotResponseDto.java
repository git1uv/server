package com.simter.domain.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ChatbotResponseDto {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetChatbotTypeResponseDto {
        String chatbot;

    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectChatbotResponseDto {
        Long counselingLogId;

    }



}
