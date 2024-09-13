package com.simter.domain.chatbot.converter;

import com.simter.domain.chatbot.dto.ClaudeRequestDto;
import com.simter.domain.chatbot.entity.ChatbotMessage;
import com.simter.domain.chatbot.entity.CounselingLog;

public class ChatbotConverter {

    public static ChatbotMessage toChatbotMessage(CounselingLog counselingLogId, String sender, ClaudeRequestDto request, String emotion) {
        return ChatbotMessage.builder()
                .counselingLog(counselingLogId)
                .sender(sender)
                .content(request.getUserMessage())
                .emotion(emotion)
                .build();
    }

}
