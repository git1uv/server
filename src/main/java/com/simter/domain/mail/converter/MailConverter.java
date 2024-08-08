package com.simter.domain.mail.converter;

import com.simter.domain.mail.dto.MailGetResponseDto;
import com.simter.domain.mail.entity.Mail;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MailConverter {

    public MailGetResponseDto convertToMailGetResponseDto(List<Mail> mails) {
        List<MailGetResponseDto.MailDto> mailDtos = mails.stream()
                .map(this::convertToMailDto)
                .collect(Collectors.toList());
        return MailGetResponseDto.builder()
                .mails(mailDtos)
                .build();
    }

    private MailGetResponseDto.MailDto convertToMailDto(Mail mail) {
        return MailGetResponseDto.MailDto.builder()
                .mailId(mail.getId())
                .isRead(mail.getIsRead())
                .content(mail.getContent())
                .chatbotType(mail.getChatbotType())
                .createdAt(mail.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString())
                .build();
    }
}
