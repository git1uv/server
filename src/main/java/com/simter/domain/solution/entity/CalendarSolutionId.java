package com.simter.domain.solution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;


@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarSolutionId implements Serializable {

    @Column(name = "calendar_id", nullable = false)
    private Long calendarId;

    @Column(name = "solution_id", nullable = false)
    private Long solutionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarSolutionId that = (CalendarSolutionId) o;
        return Objects.equals(calendarId, that.calendarId) &&
                Objects.equals(solutionId, that.solutionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calendarId, solutionId);
    }
}