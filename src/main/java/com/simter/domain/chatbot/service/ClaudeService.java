package com.simter.domain.chatbot.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClaudeService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getClaudeResponse(String userInput) {
        String apiUrl = "https://api.anthropic.com/v1/claude";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + System.getProperty("CLAUDE_API_KEY"));
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 기본 설정 프롬프트 설정
        String prompt_T = "You are a helpful assistant.";
        String prompt_F = "You are a helpful assistant.";
        String prompt_TF = "You are a helpful assistant.";
        String prompt = "You are a helpful assistant. Provide concise and accurate responses.";


        String requestBody = String.format("{\"input\": \"%s\n%s\"}", prompt, userInput);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        return response.getBody();
    }
}