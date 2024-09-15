package com.simter.domain.chatbot.repository;

import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.entity.Solution;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {

    List<Solution> findAllByCounselingLogId(Long counselingLogId);
    List<Solution> findByCounselingLog(@NotNull CounselingLog counselingLog);
}


