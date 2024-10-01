package com.simter.domain.chatbot.entity;

import com.simter.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatbotMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chatbot_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counseling_log_id")
    private CounselingLog counselingLog;

    @Column(nullable = false, length = 100)
    private String sender;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(length = 100)
    private String emotion;

    @Column(nullable = false)
    @Setter
    private boolean redFlag;


    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;


}

