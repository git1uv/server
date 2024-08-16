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
    @Column(updatable = false)
    private LocalDateTime startedAt;

    @Column(updatable = false)
    private LocalDateTime endedAt;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(nullable = false, length = 500)
    private String suggestion;

    @Column(nullable = false, length = 100)
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
