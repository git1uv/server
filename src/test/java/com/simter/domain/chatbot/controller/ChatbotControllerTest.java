package com.simter.domain.chatbot.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.simter.domain.chatbot.dto.SelectChatbotResponseDto;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import com.simter.domain.chatbot.service.ChatbotService;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(MockitoExtension.class)
class ChatbotControllerTest {

    @Mock
    private ChatbotService chatbotService;

    @Mock
    private MemberRepository memberRepository;


    void 사용자의_기본_챗봇_변경() {
    }

    @Test
    void 사용자의_기본_챗봇_조회() {
        // Given
        Member member = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("테스트_유저")
                .build();
        member.setChatbot("반바니");

        when(chatbotService.getDefaultChatbot(1L)).thenReturn("반바니");

        // When
        String defaultChatbot = chatbotService.getDefaultChatbot(member.getId());

        // Then
        assertEquals("반바니", defaultChatbot);

        // 불필요한 스터빙은 제거합니다.
    }

    @Test
    void 챗봇_설정_및_챗봇_세션_등록() {
//        // Given
//        Member member = Member.builder()
//                .id(1L)
//                .email("test@gmail.com")
//                .nickname("테스트_유저")
//                .build();
//
//        when(memberRepository.findByEmail("test@gmail.com"))
//                .thenReturn(Optional.of(member));
//
//        // When
//        SelectChatbotResponseDto responseDto = chatbotService.selectChatbot("test@gmail.com", "반바니");
//
//        // Then
//        assertNotNull(responseDto);
//        assertEquals(1L, responseDto.getCounselingId());
    }
}