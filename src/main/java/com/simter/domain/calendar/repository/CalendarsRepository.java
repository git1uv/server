package com.simter.domain.calendar.repository;

import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarsRepository extends JpaRepository<Calendars, Long> {

    boolean existsById(@NotNull Long id);
    Optional<Calendars> findById(@NotNull Long id);

    List<Calendars> findByUserIdAndDateBetween(@NotNull Member member, @NotNull LocalDate startDate, @NotNull LocalDate endDate);

    Optional<Calendars> findByUserIdAndDate(@NotNull Member member, @NotNull LocalDate date);

    boolean existsByUserIdAndDate(@NotNull Member member, @NotNull LocalDate date);
}
