package com.simter.domain.chatbot.service;

import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import io.github.cdimascio.dotenv.Dotenv;


import java.util.HashMap;
import java.util.Map;

@Service
public class ClaudeAPIService {

    private final Dotenv dotenv = Dotenv.load();

    private final WebClient webClient;

    private final String API_KEY= dotenv.get("CLAUDE_API_KEY");
    private final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";


    private String conversationContext = "";

    public ClaudeAPIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<String> chatWithClaude(String userMessage) {
        // 이전 대화 상태에 새 메시지 추가
        conversationContext += "먼저, 사용자가 요청한 메시지에 대한 감정을 단어로 적어줘. 이후 공감형(F) 답변을 적어줘. \nUser: " + userMessage + "\nAssistant:";

        // 요청 본문을 JSON 형식으로 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "claude-3-5-sonnet-20240620");
        requestBody.put("max_tokens", 1024);

        // "messages" 필드를 설정 (role과 content로 메시지 전달)
        Map<String, Object> userMessageContent = new HashMap<>();
        userMessageContent.put("role", "user");
        userMessageContent.put("content", userMessage);

        // 메시지 리스트에 사용자 메시지 추가
        requestBody.put("messages", new Object[]{userMessageContent});
        requestBody.put("system", conversationContext);

        // WebClient로 API 호출
        return webClient.post()
                .uri(CLAUDE_API_URL)
                .header("x-api-key", API_KEY)  // API Key 설정
                .header("anthropic-version", "2023-06-01")  // 버전 헤더 설정
                .header("Content-Type", "application/json")  // Content-Type 설정
                .bodyValue(requestBody)  // 요청 본문 설정
                .retrieve()
                .bodyToMono(String.class)  // 응답을 문자열로 받음
                .map(response -> {
                    // 응답에서 챗봇의 답변을 추출
                    String assistantResponse = extractAssistantResponse(response);

                    // 대화 상태에 챗봇 응답 추가
                    conversationContext += " " + assistantResponse;

                    return assistantResponse;
                });
    }

    // 응답에서 assistant 메시지 추출 (실제 구현 필요)
    private String extractAssistantResponse(String response) {
        // JSON 응답을 파싱하여 assistant 메시지를 추출
        // 실제 Claude 응답 형식에 맞게 파싱 로직을 구현해야 함
        return response;  // 임시로 전체 응답 반환
    }
}