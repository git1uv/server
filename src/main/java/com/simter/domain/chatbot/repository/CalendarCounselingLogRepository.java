package com.simter.domain.chatbot.repository;

import com.simter.domain.chatbot.entity.CalendarCounselingLog;
import com.simter.domain.chatbot.entity.CalendarCounselingLogId;
import com.simter.domain.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarCounselingLogRepository extends JpaRepository<CalendarCounselingLog, CalendarCounselingLogId> {

    boolean existsByUserIdAndCalendarsDate(@NotNull Member member, @NotNull LocalDate date);
}
