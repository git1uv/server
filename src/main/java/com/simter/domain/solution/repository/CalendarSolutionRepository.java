package com.simter.domain.solution.repository;

import com.simter.domain.solution.entity.CalendarSolution;
import com.simter.domain.solution.entity.CalendarSolutionId;
import com.simter.domain.solution.entity.Solution;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarSolutionRepository extends JpaRepository<CalendarSolution, CalendarSolutionId> {

}
