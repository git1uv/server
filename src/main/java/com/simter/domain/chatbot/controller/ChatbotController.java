package com.simter.domain.chatbot.controller;

import com.simter.apiPayload.ApiResponse;
import com.simter.apiPayload.code.status.SuccessStatus;
import com.simter.config.JwtTokenProvider;
import com.simter.domain.chatbot.dto.ChatbotRequestDto.DefaultChatbotRequestDto;
import com.simter.domain.chatbot.dto.ChatbotRequestDto.SelectChatbotRequestDto;
import com.simter.domain.chatbot.dto.ChatbotResponseDto.GetChatbotTypeResponseDto;
import com.simter.domain.chatbot.dto.ChatbotResponseDto.SelectChatbotResponseDto;
import com.simter.domain.chatbot.dto.ClaudeRequestDto;
import com.simter.domain.chatbot.dto.ClaudeResponseDto;
import com.simter.domain.chatbot.dto.CounselingResponseDto;
import com.simter.domain.chatbot.service.ChatbotService;
import com.simter.domain.chatbot.service.ClaudeAPIService;
import com.simter.domain.member.dto.JwtTokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "챗봇 API", description = "챗봇 변경, 챗봇과의 대화 API")
@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ClaudeAPIService claudeAPIService;


    //Default 챗봇 변경 API(PATCH)
    @Operation(summary = "Default 챗봇 변경", description = "챗봇 유형을 변경하는 API")
    @PatchMapping("/update")
    public ApiResponse<Void> updateDefaultChatbot(
            @RequestBody DefaultChatbotRequestDto defaultChatbotRequestDto,
            HttpServletRequest request) {
        String email = jwtTokenProvider.getEmail(jwtTokenProvider.resolveToken(request).getAccessToken());
        chatbotService.updateDefaultChatbot(email, defaultChatbotRequestDto.getChatbot());
        return ApiResponse.onSuccessCustom(SuccessStatus.CHATBOT_TYPE_CHANGE,null);
    }

    //Default 챗봇 조회 API(GET)
    @Operation(summary = "Default 챗봇 조회", description = "사용자의 default 챗봇을 조회하는 API")
    @GetMapping("")
    public ApiResponse<GetChatbotTypeResponseDto> getDefaultChatbot(
            HttpServletRequest request) {
        String email = jwtTokenProvider.getEmail(jwtTokenProvider.resolveToken(request).getAccessToken());
        GetChatbotTypeResponseDto response = chatbotService.getDefaultChatbot(email);
        return ApiResponse.onSuccessCustom(SuccessStatus.CHATBOT_TYPE_GET, response);
    }

    //특정 세션의 챗봇 type 설정(PATCH)
    @Operation(summary = "특정 세션의 챗봇 설정 변경", description = "사용자의 세션에 해당하는 챗봇 타입을 설정하고 새로운 상담 일지를 생성하는 API")
    @PostMapping("/session")
    public ApiResponse<SelectChatbotResponseDto> createChatbotSession(
            @RequestBody SelectChatbotRequestDto selectChatbotRequestDto,
            HttpServletRequest request) {
        JwtTokenDto token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getEmail(token.getAccessToken());
        SelectChatbotResponseDto response = chatbotService.selectChatbot(email, selectChatbotRequestDto.getChatbotType());
        return ApiResponse.onSuccessCustom(SuccessStatus.CHATBOT_TYPE_CHANGE, response);
    }

    @Operation(summary = "챗봇과의 대화 API", description = "챗봇 채팅 API")
    @PostMapping("/chatting")
    public Mono<ApiResponse<ClaudeResponseDto>> chatting(
            @RequestBody ClaudeRequestDto requestDto,
            @RequestParam Long counselingLogId) {
        return claudeAPIService.chatWithClaude(requestDto, counselingLogId)
                .map(response -> ApiResponse.onSuccessCustom(SuccessStatus.CHATBOT_CHATTING, response));
    }

    @Operation(summary = "챗봇과의 대화 종료", description = "채팅 종료 API")
    @GetMapping("/exit")
    public Mono<ApiResponse> chatting(
            @RequestParam Long counselingLogId) {
        return claudeAPIService.summarizeConversation(counselingLogId)
                .map(response -> ApiResponse.onSuccessCustom(SuccessStatus.CHATBOT_SESSION_END, response));
    }

    @Operation(summary = "상담 일지 조회", description = "상담 일지 조회 API")
    @GetMapping("/counselinglog/{counselinglogId}")
    public ApiResponse<CounselingResponseDto.CounselingDto> getCounselingLog(
            @PathVariable Long counselinglogId) {
        CounselingResponseDto.CounselingDto response = chatbotService.getCounselingLog(counselinglogId);
        return ApiResponse.onSuccessCustom(SuccessStatus.COUNSELING_LIST, response);
    }




}