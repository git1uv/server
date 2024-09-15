package com.simter.domain.chatbot.entity;

import com.simter.domain.member.entity.Member;
import com.simter.domain.solution.entity.Solution;
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
    private Member user;

    @Column(nullable = false, length = 100)
    private String chatbotType;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime startedAt;

    @Column(updatable = false)
    private LocalDateTime endedAt;

    @Column(length = 500)
    private String summary;

    @Column(length = 500)
    private String suggestion;

    @Column(length = 100)
    private String title;


    public void setMember(Long memberId) {
        this.user.setId(memberId);
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
