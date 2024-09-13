package com.simter.domain.chatbot.service;

import com.simter.domain.chatbot.converter.ChatbotConverter;
import com.simter.domain.chatbot.dto.ClaudeRequestDto;
import com.simter.domain.chatbot.dto.ClaudeResponseDto;
import com.simter.domain.chatbot.entity.ChatbotMessage;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.repository.ChatbotRepository;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.HashMap;
import java.util.Map;
import reactor.core.publisher.Mono;

@Service
public class ClaudeAPIService {

    private final Dotenv dotenv;
    private final WebClient webClient;
    private final String API_KEY;
    private final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private final ChatbotRepository chatbotRepository;
    private final CounselingLogRepository counselingLogRepository;

    // 대화 상태를 유지할 변수
    private String conversationContext = "";

    @Autowired
    public ClaudeAPIService(WebClient.Builder webClientBuilder, ChatbotRepository chatbotRepository, CounselingLogRepository counselingLogRepository) {
        this.webClient = webClientBuilder.build();
        this.dotenv = Dotenv.load();
        this.API_KEY = dotenv.get("CLAUDE_API_KEY");
        this.chatbotRepository = chatbotRepository;
        this.counselingLogRepository = counselingLogRepository;
    }

    public Mono<ClaudeResponseDto> chatWithClaude(ClaudeRequestDto request, Long counselingLogId) {
        CounselingLog counselingLog = counselingLogRepository.findById(counselingLogId)
                .orElseThrow(() -> new RuntimeException("Counseling Log not found"));

        // 이전 대화에 새로운 대화를 추가
        conversationContext += "User: " + request.getUserMessage() + "\nAssistant:";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "claude-3-5-sonnet-20240620");
        requestBody.put("max_tokens", 1024);

        Map<String, Object> userMessageContent = new HashMap<>();
        userMessageContent.put("role", "user");
        userMessageContent.put("content", request.getUserMessage());

        requestBody.put("messages", new Object[]{userMessageContent});
        requestBody.put("system", conversationContext);

        return webClient.post()
                .uri(CLAUDE_API_URL)
                .header("x-api-key", API_KEY)
                .header("anthropic-version", "2023-06-01")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    String assistantResponse = extractAssistantResponse(response);
                    String emotion = extractEmotion(assistantResponse);

                    // 이전 대화에 Assistant 응답 추가
                    conversationContext += " " + assistantResponse;

                    ChatbotMessage userMessage = ChatbotConverter.toUserMessage(counselingLog, "USER", request, emotion);
                    chatbotRepository.save(userMessage);

                    ChatbotMessage assistantMessage = ChatbotConverter.toAssistantMessage(counselingLog, assistantResponse, emotion);
                    chatbotRepository.save(assistantMessage);

                    return new ClaudeResponseDto(emotion, assistantResponse);
                });
    }

    private String extractAssistantResponse(String response) {
        return response;
    }

    private String extractEmotion(String response) {
        return "happy";
    }
}
