package com.simter.domain.chatbot.controller;

import com.simter.domain.chatbot.service.ClaudeService;
import com.simter.domain.chatbot.service.ComprehendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatbotSessionController {

    private final ClaudeService claudeService;
    private final ComprehendService sentimentAnalysisService;

    @Autowired
    public ChatbotSessionController(ClaudeService claudeService, ComprehendService sentimentAnalysisService) {
        this.claudeService = claudeService;
        this.sentimentAnalysisService = sentimentAnalysisService;
    }

    @PostMapping("/message")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> payload) {
        String userInput = payload.get("message");
        String claudeResponse = claudeService.getClaudeResponse(userInput);

        String sentimentAnalysis = sentimentAnalysisService.analyzeSentiment(claudeResponse);

        Map<String, String> result = new HashMap<>();
        result.put("ClaudeResponse", claudeResponse);
        result.put("SentimentAnalysis", sentimentAnalysis);

        return ResponseEntity.ok(result);
    }
}
