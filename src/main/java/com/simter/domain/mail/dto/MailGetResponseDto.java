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
    public static class MailDto{
        private final Long mailId;
        private final boolean isRead;
        private final String content;
        private final String chatbotType;
        private final String createdAt;
    }

}
