package com.simter.domain.chatbot.controller;

import com.simter.apiPayload.ApiResponse;
import com.simter.domain.chatbot.dto.DefaultChatbotRequestDto;
import com.simter.domain.chatbot.service.ChatbotService;
import com.simter.domain.mail.dto.MailDeleteRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "챗봇 API", description = "챗봇 변경, 챗봇과의 대화 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    //Default 챗봇 변경 API(PATCH)
    @Operation(summary = "Default 챗봇 변경", description = "챗봇 유형을 변경하는 API")
    @PatchMapping("/chatbot/update/{userId}")
    public ApiResponse<Void> updateDefaultChatbot(@PathVariable Long userId, @RequestBody DefaultChatbotRequestDto defaultChatbotRequestDto) {
        chatbotService.updateDefaultChatbot(userId, defaultChatbotRequestDto.getChatbot());
        return ApiResponse.onSuccess(null);
    }

}
