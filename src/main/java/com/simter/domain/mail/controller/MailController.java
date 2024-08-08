package com.simter.domain.mail.controller;


import com.simter.apiPayload.ApiResponse;
import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.mail.converter.MailConverter;
import com.simter.domain.mail.dto.MailGetResponseDto;
import com.simter.domain.mail.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "편지 API", description = "편지를 조회하고 삭제, 즐겨찾기를 등록하는 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    // 편지 전체조회 API (GET)
    @Operation(summary = "편지 전체 조회", description = "편지를 '모두보기'로 조회하는 API")
    @GetMapping("/mail/{memberId}")
    public ApiResponse<MailGetResponseDto> getAllMails(@PathVariable Long memberId) {
        MailGetResponseDto response = mailService.getAllMails(memberId);
        return ApiResponse.onSuccess(response);
    }

    // 편지 즐겨찾기 조회 API (GET)
    @Operation(summary = "편지 즐겨찾기 조회", description = "편지를 '즐겨찾기'로 조회하는 API")
    @GetMapping("/mail/stared/{memberId}")
    public ApiResponse<MailGetResponseDto> getStaredMails(@PathVariable Long memberId) {
        MailGetResponseDto response = mailService.getStaredMails(memberId);
        return ApiResponse.onSuccess(response);
    }


}
