package com.simter.domain.chatbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CalendarCounselingLogId implements Serializable {

    @Column(name = "calendar_id")
    private Long calendarId;

    @Column(name = "counseling_log_id")
    private Long counselingLogId;

    // equals와 hashCode 메서드
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarCounselingLogId that = (CalendarCounselingLogId) o;
        return Objects.equals(calendarId, that.calendarId) &&
                Objects.equals(counselingLogId, that.counselingLogId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calendarId, counselingLogId);
    }
}