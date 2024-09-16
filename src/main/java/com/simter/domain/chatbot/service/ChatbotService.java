package com.simter.domain.chatbot.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.chatbot.converter.ChatbotConverter;
import com.simter.domain.chatbot.dto.ChatbotResponseDto;
import com.simter.domain.chatbot.dto.ChatbotResponseDto.GetChatbotTypeResponseDto;
import com.simter.domain.chatbot.dto.ChatbotResponseDto.SelectChatbotResponseDto;
import com.simter.domain.chatbot.dto.CounselingResponseDto;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.entity.Solution;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import com.simter.domain.chatbot.repository.SolutionRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotService {

   private final MemberRepository memberRepository;
   private final CounselingLogRepository counselingLogRepository;
   private final SolutionRepository solutionRepository;

    //사용자의 default 챗봇 변경
    @Transactional
    public void updateDefaultChatbot(String email, String ChatbotType) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        member.setChatbot(ChatbotType);
        memberRepository.save(member);

    }

    //사용자의 default 챗봇 조회
    public GetChatbotTypeResponseDto getDefaultChatbot(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return new ChatbotResponseDto.GetChatbotTypeResponseDto(member.getChatbot());
    }

    //챗봇 세션을 시작을 시작하고 해당 세션의 챗봇 설정
    @Transactional
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
    public CounselingResponseDto.CounselingDto getCounselingLog(Long counselingLogId) {
        CounselingLog counselingLog = counselingLogRepository.findById(counselingLogId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.COUNSELING_LOG_NOT_FOUND));
        List<Solution> solution = solutionRepository.findAllByCounselingLogId(counselingLog.getId());
        return ChatbotConverter.toCounselingDto(counselingLog,solution);
    }


}
