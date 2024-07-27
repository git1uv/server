package com.simter.domain.calendar.entity;

import com.simter.domain.chatbot.entity.CalendarCounselingLog;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.member.entity.Member;
import com.simter.domain.solution.entity.CalendarSolution;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Calendars {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="calendar_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member userId;

    private LocalDate date;

    @Column(length = 100)
    private String emotion;

    @Column(length = 300)
    private String diary;

    @OneToMany(mappedBy = "calendars", cascade = CascadeType.ALL)
    private List<CalendarSolution> calendarSolutionList = new ArrayList<>();

    @OneToMany(mappedBy = "calendars", cascade = CascadeType.ALL)
    private List<CalendarCounselingLog> calendarCounselingLogList = new ArrayList<>();





}