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
    public MailGetResponseDto getAllMails(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Mail> mails = mailRepository.findByMemberAndIsDeletedFalse(member);
        return mailConverter.convertToMailGetResponseDto(mails);
    }

    //특정 사용자의 즐겨찾기한 메일 조회
    public MailGetResponseDto getStaredMails(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Mail> mails = mailRepository.findByMemberAndIsStaredTrue(member);
        return mailConverter.convertToMailGetResponseDto(mails);
    }





}
