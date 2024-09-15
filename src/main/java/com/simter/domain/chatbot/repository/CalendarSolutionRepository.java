package com.simter.domain.chatbot.repository;


import com.simter.domain.chatbot.entity.CalendarSolution;
import com.simter.domain.chatbot.entity.CalendarSolutionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarSolutionRepository extends JpaRepository<CalendarSolution, CalendarSolutionId> {

}