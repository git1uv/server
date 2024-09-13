package com.simter.domain.chatbot.service;

import com.simter.domain.chatbot.converter.ChatbotConverter;
import com.simter.domain.chatbot.dto.ClaudeRequestDto;
import com.simter.domain.chatbot.dto.ClaudeResponseDto;
import com.simter.domain.chatbot.entity.ChatbotMessage;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.repository.ChatbotRepository;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    public ClaudeAPIService(WebClient.Builder webClientBuilder, ChatbotRepository chatbotRepository, CounselingLogRepository counselingLogRepository) {
        this.webClient = webClientBuilder.build();
        this.dotenv = Dotenv.load();
        this.API_KEY = dotenv.get("CLAUDE_API_KEY");
        this.chatbotRepository = chatbotRepository;
        this.counselingLogRepository = counselingLogRepository;
    }

    public Mono<ClaudeResponseDto> chatWithClaude(ClaudeRequestDto request, Long counselingLogId) {
        // 1. counseling_log_id로 상담 기록을 찾고 chatbot_type 가져오기
        CounselingLog counselingLog = counselingLogRepository.findById(counselingLogId)
                .orElseThrow(() -> new RuntimeException("Counseling Log not found"));

        String chatbotType = counselingLog.getChatbotType();

        // 2. chatbot_type에 맞는 프롬프트 선택
        String systemPrompt = selectSystemPrompt(chatbotType);

        // 3. 이전 사용자 메시지를 DB에서 가져와 conversationContext 구성
        String previousMessages = getPreviousUserMessages(counselingLogId);

        // 4. 대화 상태에 사용자 메시지 추가
        String conversationContext = previousMessages + "\nUser: " + request.getUserMessage() + "\nAssistant:";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "claude-3-5-sonnet-20240620");
        requestBody.put("max_tokens", 1024);

        Map<String, Object> userMessageContent = new HashMap<>();
        userMessageContent.put("role", "user");
        userMessageContent.put("content", request.getUserMessage());

        requestBody.put("messages", new Object[]{userMessageContent});
        requestBody.put("system", systemPrompt + "\n" + conversationContext);

        // 5. WebClient로 API 호출
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

                    // 사용자 메시지와 챗봇 응답을 DB에 저장
                    ChatbotMessage userMessage = ChatbotConverter.toUserMessage(counselingLog, "USER", request, emotion);
                    chatbotRepository.save(userMessage);

                    ChatbotMessage assistantMessage = ChatbotConverter.toAssistantMessage(counselingLog, assistantResponse, emotion);
                    chatbotRepository.save(assistantMessage);

                    return new ClaudeResponseDto(emotion, assistantResponse);
                });
    }

    // chatbot_type에 맞는 프롬프트 선택
    private String selectSystemPrompt(String chatbotType) {
        switch (chatbotType) {
            case "F":
                return "공감형(F) 대답을 해줘.";
            case "T":
                return "현실적인 조언형(T) 대답을 해줘.";
            case "H":
            default:
                return "공감과 현실을 섞은(H) 대답을 해줘.";
        }
    }

    // 이전 사용자 메시지 가져오기
    private String getPreviousUserMessages(Long counselingLogId) {
        List<ChatbotMessage> previousMessages = chatbotRepository.findByCounselingLogId(counselingLogId);
        StringBuilder messages = new StringBuilder();

        for (ChatbotMessage message : previousMessages) {
            messages.append(message.getSender()).append(": ").append(message.getContent()).append("\n");
        }

        return messages.toString();
    }

    private String extractAssistantResponse(String response) {
        return response;
    }

    private String extractEmotion(String response) {
        return "happy";
    }
}
