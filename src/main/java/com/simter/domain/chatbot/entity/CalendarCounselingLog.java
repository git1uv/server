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
    @MapsId("calendarId")
    @JoinColumn(name = "calendar_id", insertable = false, updatable = false)
    private Calendars calendars;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("counselingLogId")
    @JoinColumn(name = "counseling_log_id", insertable = false, updatable = false)
    private CounselingLog counselingLog;



}
