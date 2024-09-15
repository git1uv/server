package com.simter.domain.chatbot.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.chatbot.dto.SelectChatbotResponseDto;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotService {

   private final MemberRepository memberRepository;
   private final CounselingLogRepository counselingLogRepository;

    //사용자의 default 챗봇 변경
    public void updateDefaultChatbot(Long memberId, String ChatbotType) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        member.setChatbot(ChatbotType);
        memberRepository.save(member);

    }

    //사용자의 default 챗봇 조회
    public String getDefaultChatbot(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return member.getChatbot();
    }

    //챗봇 세션을 시작을 시작하고 해당 세션의 챗봇 설정
    public SelectChatbotResponseDto selectChatbot(String email, String chatbotType) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        CounselingLog log = CounselingLog.builder()
                .user(member)
                .chatbotType(chatbotType)
                .startedAt(LocalDateTime.now())
                .build();

        log = counselingLogRepository.save(log);
        return new SelectChatbotResponseDto(log.getId());
    }

    //상담일지 가져오기
    public String getCounselingLog(Long counselingLogId) {
        CounselingLog log = counselingLogRepository.findById(counselingLogId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHATBOT_SESSION_NOT_FOUND));
        return log.getSummary();
    }


}
