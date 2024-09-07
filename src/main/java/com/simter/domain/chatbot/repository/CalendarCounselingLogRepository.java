package com.simter.domain.chatbot.repository;

import com.simter.domain.chatbot.entity.CalendarCounselingLog;
import com.simter.domain.chatbot.entity.CalendarCounselingLogId;
import com.simter.domain.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalendarCounselingLogRepository extends JpaRepository<CalendarCounselingLog, CalendarCounselingLogId> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CalendarCounselingLog c " +
        "WHERE c.counselingLog.user = :user AND c.calendars.date = :calendarsDate")
    boolean existsByUserAndCalendarsDate(@Param("user") Member user, @Param("calendarsDate") LocalDate calendarsDate);
}

