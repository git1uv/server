package com.simter.domain.mail.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MailGetResponseDto {
    private List<MailDto> mails;

    @Builder
    @Getter
    public static class MailDto {
        private Long mailId;
        private boolean isRead;
        private boolean isStarred;
        private String content;
        private String chatbotType;
        private String createdAt;
    }

}
