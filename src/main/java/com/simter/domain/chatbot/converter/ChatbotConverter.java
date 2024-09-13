package com.simter.domain.chatbot.converter;

import com.simter.domain.chatbot.dto.ClaudeRequestDto;
import com.simter.domain.chatbot.dto.ClaudeResponseDto;
import com.simter.domain.chatbot.entity.ChatbotMessage;
import com.simter.domain.chatbot.entity.CounselingLog;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatbotConverter {

    public static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    public static ChatbotMessage toUserMessage(CounselingLog counselingLogId, ClaudeRequestDto request) {
        return ChatbotMessage.builder()
                .counselingLog(counselingLogId)
                .sender("USER")
                .content(request.getUserMessage())
                .build();
    }

    public static ChatbotMessage toAssistantMessage(CounselingLog counselingLog, String assistantResponse, String emotion) {
        return ChatbotMessage.builder()
                .counselingLog(counselingLog)
                .sender("ASSISTANT")
                .content(assistantResponse)
                .emotion(emotion)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ClaudeResponseDto toClaudeResponseDto(ChatbotMessage chatbotMessage) {
        return ClaudeResponseDto.builder()
                .counselingId(chatbotMessage.getCounselingLog().getId())
                .emotion(chatbotMessage.getEmotion())
                .message(chatbotMessage.getContent())
                .createdAt(formatDateTime(chatbotMessage.getCreatedAt()))
                .build();
    }

}
