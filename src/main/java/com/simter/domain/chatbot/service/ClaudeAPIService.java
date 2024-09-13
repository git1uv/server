package com.simter.domain.chatbot.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.chatbot.converter.ChatbotConverter;
import com.simter.domain.chatbot.dto.ClaudeRequestDto;
import com.simter.domain.chatbot.dto.ClaudeResponseDto;
import com.simter.domain.chatbot.entity.ChatbotMessage;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.repository.ChatbotRepository;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
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
        CounselingLog counselingLog = counselingLogRepository.findById(counselingLogId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHATBOT_SESSION_NOT_FOUND));
        String chatbotType = counselingLog.getChatbotType();

        //프롬프트 선택
        String systemPrompt = selectSystemPrompt(chatbotType);

        systemPrompt += " 사용자의 대화를 읽고 9개의 감정 중 하나를 반환해준 뒤 대화를 해줘: 기쁨, 슬픔, 화남, 놀람, 공포, 혐오, 기대, 신뢰, 사랑.";

        // 이전 대화 내역 가져오기
        String previousMessages = getPreviousUserMessages(counselingLogId);
        String conversationContext = previousMessages + "\nUser: " + request.getUserMessage() + "\nAssistant:";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "claude-3-5-sonnet-20240620");
        requestBody.put("max_tokens", 1024);

        Map<String, Object> userMessageContent = new HashMap<>();
        userMessageContent.put("role", "user");
        userMessageContent.put("content", request.getUserMessage());

        requestBody.put("messages", new Object[]{userMessageContent});
        requestBody.put("system", systemPrompt + "\n" + conversationContext);

        // Claude API 호출
        return webClient.post()
                .uri(CLAUDE_API_URL)
                .header("x-api-key", API_KEY)
                .header("anthropic-version", "2023-06-01")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    // 응답을 JSON 파싱
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray contentArray = jsonResponse.getJSONArray("content");
                    String assistantResponseText = contentArray.getJSONObject(0).getString("text");

                    String emotion = extractEmotion(assistantResponseText);

                    // 사용자 메시지와 챗봇 응답을 DB에 저장
                    ChatbotMessage userMessage = ChatbotConverter.toUserMessage(counselingLog, "USER", request, emotion);
                    chatbotRepository.save(userMessage);

                    ChatbotMessage assistantMessage = ChatbotConverter.toAssistantMessage(counselingLog, assistantResponseText, emotion);
                    chatbotRepository.save(assistantMessage);

                    return new ClaudeResponseDto(emotion, assistantResponseText);
                });

    }

    // chatbot_type에 맞는 프롬프트 선택
    private String selectSystemPrompt(String chatbotType) {
        switch (chatbotType) {
            case "F":
                return "너는 심리상담가야. 사용자에게 최대한 공감을 해주고 희망찬 대답을 해줘.";
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
        if (response.contains("기쁨")) {
            return "기쁨";
        } else if (response.contains("슬픔")) {
            return "슬픔";
        } else if (response.contains("화남")) {
            return "화남";
        } else if (response.contains("놀람")) {
            return "놀람";
        } else if (response.contains("공포")) {
            return "공포";
        } else if (response.contains("혐오")) {
            return "혐오";
        } else if (response.contains("기대")) {
            return "기대";
        } else if (response.contains("신뢰")) {
            return "신뢰";
        } else if (response.contains("사랑")) {
            return "사랑";
        } else {
            return "기타";  // 감정을 찾지 못한 경우
        }
    }
}
