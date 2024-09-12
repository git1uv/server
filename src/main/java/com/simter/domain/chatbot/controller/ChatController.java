package com.simter.domain.chatbot.controller;

import com.simter.domain.chatbot.service.ClaudeAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("api/v1")
public class ChatController {

    @Autowired
    private ClaudeAPIService claudeApiService;

    // 사용자 ID와 메시지를 받아 Claude와 대화
    @PostMapping("/message")
    public Mono<String> chat(@RequestParam String userMessage) {
        return claudeApiService.chatWithClaude(userMessage);
    }
}