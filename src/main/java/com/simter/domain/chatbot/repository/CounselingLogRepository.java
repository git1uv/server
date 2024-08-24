package com.simter.domain.chatbot.repository;

import com.simter.domain.chatbot.entity.CounselingLog;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounselingLogRepository extends JpaRepository<CounselingLog, Long> {

    List<CounselingLog> findCounselingLogByUserIdAndDate (@NotNull Long userId, @NotNull LocalDate date);

}
