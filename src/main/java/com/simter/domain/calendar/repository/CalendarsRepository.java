package com.simter.domain.calendar.repository;

import com.simter.domain.calendar.entity.Calendars;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarsRepository extends JpaRepository<Calendars, Long> {

    Optional<Calendars> findById(@NotNull Long id);

    List<Calendars> findByUserIdAndDateBetween(@NotNull Long userId, @NotNull LocalDate startDate, @NotNull LocalDate endDate);

}
