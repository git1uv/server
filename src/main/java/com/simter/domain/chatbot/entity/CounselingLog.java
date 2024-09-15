package com.simter.domain.chatbot.entity;

import com.simter.domain.member.entity.Member;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CounselingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="counseling_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @Column(nullable = false, length = 100)
    private String chatbotType;

    @CreatedDate
    @Column(nullable = true)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(nullable = true)
    private LocalDateTime endedAt;

    @Column(length = 500)
    private String summary;

    @Column(length = 500)
    private String suggestion;

    @Column(length = 30)
    private String title;




    public void setMember(Long memberId) {
        this.member = member;
    }

    public void setChatbotType(String chatbotType) {
        this.chatbotType = chatbotType;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

}
