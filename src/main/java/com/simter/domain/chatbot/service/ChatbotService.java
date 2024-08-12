package com.simter.domain.chatbot.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotService {

   private final MemberRepository memberRepository;

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

}
