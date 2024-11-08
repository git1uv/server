package com.simter.domain.chatbot.repository;

import com.simter.domain.chatbot.entity.ChatbotMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatbotRepository extends JpaRepository<ChatbotMessage, Long> {

    List<ChatbotMessage> findByCounselingLogId(Long counselingLogId);
    void deleteByCounselingLogId(Long counselingLogId);

}
