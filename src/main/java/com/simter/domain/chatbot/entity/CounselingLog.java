package com.simter.domain.chatbot.entity;

import com.simter.domain.member.entity.Member;
import com.simter.domain.solution.entity.Solution;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "counselingLog", cascade = CascadeType.ALL)
    private List<ChatbotMessage> chatbotMessagesList = new ArrayList<>();

    @OneToMany(mappedBy = "counselingLog", cascade = CascadeType.ALL)
    private List<CalendarCounselingLog> calendarCounselingLogList = new ArrayList<>();

    @OneToMany(mappedBy = "counselingLog", cascade = CascadeType.ALL)
    private List<Solution> solutionList = new ArrayList<>();


}
