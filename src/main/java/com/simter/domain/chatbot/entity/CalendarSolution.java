package com.simter.domain.chatbot.entity;

import com.simter.domain.calendar.entity.Calendars;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarSolution {

    @EmbeddedId
    private CalendarSolutionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false, insertable=false, updatable=false)
    private Calendars calendar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false, insertable=false, updatable=false)
    private Solution solution;
}

