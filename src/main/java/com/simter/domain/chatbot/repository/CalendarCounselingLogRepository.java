package com.simter.domain.chatbot.repository;

import com.simter.domain.chatbot.entity.CalendarCounselingLog;
import com.simter.domain.chatbot.entity.CalendarCounselingLogId;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarCounselingLogRepository extends JpaRepository<CalendarCounselingLog, CalendarCounselingLogId> {

    List<CalendarCounselingLog> findByCalendarsDate(@NotNull LocalDate date);

    boolean existsByUserIdAndCalendarsDate(@NotNull Long userId, @NotNull LocalDate date);
}
