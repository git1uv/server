package com.simter.domain.solution.repository;

import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.solution.entity.Solution;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findByCounselingLog(@NotNull CounselingLog counselingLog);
}
