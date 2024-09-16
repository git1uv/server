package com.simter.domain.mail.controller;

import com.simter.apiPayload.ApiResponse;
import com.simter.apiPayload.code.status.SuccessStatus;
import com.simter.domain.mail.dto.MailDeleteRequestDto;
import com.simter.domain.mail.dto.MailGetResponseDto;
import com.simter.domain.mail.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "편지 API", description = "편지를 조회하고 삭제, 즐겨찾기를 등록하는 API")
@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    // 편지 전체조회 API (GET)
    @Operation(summary = "편지 전체 조회", description = "편지를 '모두보기'로 조회하는 API")
    @GetMapping("/list/{memberId}")
    public ApiResponse<MailGetResponseDto> getAllMails(@PathVariable Long memberId) {
        MailGetResponseDto response = mailService.getAllMails(memberId);
        return ApiResponse.onSuccessCustom(SuccessStatus.MAIL_LIST,response);
    }

    // 편지 즐겨찾기 조회 API (GET)
    @Operation(summary = "편지 즐겨찾기 조회", description = "편지를 '즐겨찾기'로 조회하는 API")
    @GetMapping("/starred/{memberId}")
    public ApiResponse<MailGetResponseDto> getStarredMails(@PathVariable Long memberId) {
        MailGetResponseDto response = mailService.getStarredMails(memberId);
        return ApiResponse.onSuccessCustom(SuccessStatus.MAIL_LIST,response);
    }

    // 편지 즐겨찾기 변경 API (PATCH)
    @Operation(summary = "편지 즐겨찾기 변경", description = "특정 편지의 '즐겨찾기' 여부를 변경하는 API")
    @PatchMapping("/star/{mailId}")
    public ApiResponse<Void> changeStarred(@PathVariable Long mailId) {
        mailService.changeStarred(mailId);
        return ApiResponse.onSuccessCustom(SuccessStatus.MAIL_STARRED,null);
    }

    // 편지 삭제 API (PATCH)
    @Operation(summary = "편지 삭제", description = "특정 편지를 삭제하는 API")
    @PostMapping("/delete")
    public ApiResponse<Void> deleteMail(@RequestBody MailDeleteRequestDto mailDeleteRequestDto) {
        mailService.deleteMails(mailDeleteRequestDto.getMailIds());
        return ApiResponse.onSuccessCustom(SuccessStatus.MAIL_DELETE,null);
    }

    @Operation(summary = "편지 개별 조회", description = "하나의 편지 내용을 조회하는 API")
    @GetMapping("")
    public ApiResponse<MailGetResponseDto.MailDto> getMail(
            @RequestParam Long mailId
    ) {
        MailGetResponseDto.MailDto response = mailService.getMail(mailId);
        return ApiResponse.onSuccessCustom(SuccessStatus.MAIL_GET, response);
    }
}
