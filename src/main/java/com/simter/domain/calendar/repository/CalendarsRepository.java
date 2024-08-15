package com.simter.domain.calendar.repository;

import com.simter.domain.calendar.entity.Calendars;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarsRepository extends JpaRepository<Calendars, Long> {

    Optional<Calendars> findById(@NotNull Long id);

}
