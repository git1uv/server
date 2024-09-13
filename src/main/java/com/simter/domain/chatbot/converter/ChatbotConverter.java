package com.simter.domain.chatbot.converter;

import com.simter.domain.chatbot.dto.ClaudeRequestDto;
import com.simter.domain.chatbot.entity.ChatbotMessage;
import com.simter.domain.chatbot.entity.CounselingLog;
import java.time.LocalDateTime;

public class ChatbotConverter {

    public static ChatbotMessage toUserMessage(CounselingLog counselingLogId, String sender, ClaudeRequestDto request) {
        return ChatbotMessage.builder()
                .counselingLog(counselingLogId)
                .sender(sender)
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

}
