package com.simter.domain.chatbot.entity;
import com.simter.domain.calendar.entity.Calendars;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CalendarCounselingLog {

    @EmbeddedId
    private CalendarCounselingLogId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendars calendars;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counseling_log_id")
    private CounselingLog counselingLog;



}
