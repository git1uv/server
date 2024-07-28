package com.simter.domain.solution.entity;

import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.chatbot.entity.CounselingLog;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="solution_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "counseling_log_id")
    private CounselingLog counselingLog;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isCompleted;

    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL)
    private List<CalendarSolution> calendarSolutionList = new ArrayList<>();


}
