package com.simter.domain.mail.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.mail.controller.MailController;
import com.simter.domain.mail.converter.MailConverter;
import com.simter.domain.mail.dto.MailGetResponseDto;
import com.simter.domain.mail.entity.Mail;
import com.simter.domain.mail.repository.MailRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    private final MailConverter mailConverter;

    //특정 사용자의 전체 메일(삭제되지 않은 메일) 조회
    public MailGetResponseDto getAllMails(String email, String listType) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Mail> mails;

        if (listType == null || listType.equals("all")) {
            mails = mailRepository.findByMemberAndIsDeletedFalseAndCreatedAtBefore(member, LocalDateTime.now());
        } else if (listType.equals("starred")) {
            mails = mailRepository.findByMemberAndIsDeletedFalseAndIsStarredTrueAndCreatedAtBefore(member, LocalDateTime.now());
        } else if (listType.equals("notRead")) {
            mails = mailRepository.findByMemberAndIsDeletedFalseAndIsReadFalseAndCreatedAtBefore(member, LocalDateTime.now());
        } else {
            throw new ErrorHandler(ErrorStatus.INVALID_LIST_TYPE);
        }

        if (mails.isEmpty()) {
            throw new ErrorHandler(ErrorStatus.MAIL_NOT_FOUND);
        }
        return mailConverter.convertToMailGetResponseDto(mails);
    }

    //특정 사용자의 즐겨찾기한 메일 조회
    public MailGetResponseDto getStarredMails(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Mail> mails = mailRepository.findByMemberAndIsStarredTrueAndCreatedAtBefore(member, LocalDateTime.now());
        return mailConverter.convertToMailGetResponseDto(mails);
    }

    //특정 메일의 즐겨찾기 여부 변경
    public void changeStarred(Long mailId) {
        Mail mail = mailRepository.findByIdAndIsDeletedFalseAndCreatedAtBefore(mailId, LocalDateTime.now())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MAIL_NOT_FOUND));
        mail.setIsStarred();
        mailRepository.save(mail);
    }

    //편지 삭제
    @Transactional
    public void deleteMails(List<Long> mailIds) {
        for (Long mailId : mailIds) {
            Mail mail = mailRepository.findByIdAndIsDeletedFalseAndCreatedAtBefore(mailId, LocalDateTime.now())
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.MAIL_NOT_FOUND));
            mail.markAsDeleted();
            mailRepository.save(mail);
        }
    }

    //특정 메일 조회
    @Transactional
    public MailGetResponseDto.MailDto getMail(Long mailId) {
        Mail mail = mailRepository.findByIdAndIsDeletedFalseAndCreatedAtBefore(mailId, LocalDateTime.now())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MAIL_NOT_FOUND));
        mail.setIsRead(true);
        return mailConverter.convertToMailDto(mail);
    }



}
