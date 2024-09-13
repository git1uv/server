package com.simter.domain.chatbot.repository;

import com.simter.domain.chatbot.entity.ChatbotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotRepository extends JpaRepository<ChatbotMessage, Long> {

}
