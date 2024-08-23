package com.simter.domain.chatbot.repository;

import com.simter.domain.chatbot.entity.CounselingLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounselingLogRepository extends JpaRepository<CounselingLog, Long> {


}
