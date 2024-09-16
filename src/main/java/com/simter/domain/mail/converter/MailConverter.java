package com.simter.domain.mail.converter;

import com.simter.domain.mail.dto.MailGetResponseDto;
import com.simter.domain.mail.entity.Mail;
import com.simter.domain.member.entity.Member;
import java.time.LocalDateTime;
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

    public MailGetResponseDto.MailDto convertToMailDto(Mail mail) {
        return MailGetResponseDto.MailDto.builder()
                .mailId(mail.getId())
                .isRead(mail.getIsRead())
                .isStarred(mail.getIsStarred())
                .content(mail.getContent())
                .chatbotType(mail.getChatbotType())
                .createdAt(mail.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString())
                .build();
    }

    public static Mail toMailEntity(Member member, String content, String chatbotType) {
        return Mail.builder()
                .member(member)
                .content(content)
                .chatbotType(chatbotType)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isDeleted(false)
                .isStarred(false)
                .build();
    }

}
