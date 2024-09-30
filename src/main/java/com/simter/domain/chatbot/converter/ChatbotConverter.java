package com.simter.domain.chatbot.converter;

import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.chatbot.dto.ClaudeRequestDto;
import com.simter.domain.chatbot.dto.ClaudeResponseDto;
import com.simter.domain.chatbot.dto.CounselingResponseDto;
import com.simter.domain.chatbot.dto.CounselingResponseDto.CounselingDto;
import com.simter.domain.chatbot.entity.ChatbotMessage;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.entity.Solution;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    public static ChatbotMessage toAssistantMessage(CounselingLog counselingLog, String assistantResponse, String emotion, boolean redFlag) {
        return ChatbotMessage.builder()
                .counselingLog(counselingLog)
                .sender("ASSISTANT")
                .content(assistantResponse)
                .emotion(emotion)
                .redFlag(redFlag)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ClaudeResponseDto toClaudeResponseDto(ChatbotMessage chatbotMessage) {
        return ClaudeResponseDto.builder()
                .counselingLogId(chatbotMessage.getCounselingLog().getId())
                .redFlag(chatbotMessage.isRedFlag() ? "true" : "false")
                .emotion(chatbotMessage.getEmotion())
                .message(chatbotMessage.getContent())
                .createdAt(formatDateTime(chatbotMessage.getCreatedAt()))
                .build();
    }

    public static CounselingResponseDto.Solution toSolutionDto(Solution solution) {
        return CounselingResponseDto.Solution.builder()
                .solutionId(solution.getId())
                .content(solution.getContent())
                .build();
    }

    public static List<CounselingResponseDto.Solution> toSolutionDtoList(List<Solution> solutions) {
        return solutions.stream()
                .map(ChatbotConverter::toSolutionDto)
                .collect(Collectors.toList());
    }

    public static CounselingResponseDto.CounselingDto toCounselingDto(CounselingLog counselingLog, List<Solution> solutions) {
        return CounselingDto.builder()
                .counselingLogId(counselingLog.getId())
                .chatbotType(counselingLog.getChatbotType())
                .title(counselingLog.getTitle())
                .summary(counselingLog.getSummary())
                .suggestion(counselingLog.getSuggestion())
                .endedAt(formatDateTime(counselingLog.getEndedAt()))
                .solutions(toSolutionDtoList(solutions))
                .calendarId(counselingLog.getCalendars().getId())
                .build();
    }

    public static CounselingLog updateCounselingLog(CounselingLog existingLog, String title, String userSummary, String assistantSummary, Calendars calendars) {
        return CounselingLog.builder()
                .id(existingLog.getId())
                .startedAt(existingLog.getStartedAt())
                .user(existingLog.getUser())
                .chatbotType(existingLog.getChatbotType())
                .title(title)
                .summary(userSummary)
                .suggestion(assistantSummary)
                .endedAt(LocalDateTime.now())
                .calendars(calendars)
                .build();
    }

    public static Solution createSolution(CounselingLog counselingLog, String content) {
        return Solution.builder()
                .content(content)
                .isCompleted(false)
                .counselingLog(counselingLog)
                .build();
    }








}
