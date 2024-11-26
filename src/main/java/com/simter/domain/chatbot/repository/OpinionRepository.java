package com.simter.domain.chatbot.repository;

import com.simter.domain.chatbot.entity.Opinion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {
    @Query("SELECT o FROM Opinion o WHERE o.member.email = :email ORDER BY o.createdAt DESC")
    List<Opinion> findTop3ByMemberEmailOrderByCreatedAtDesc(@Param("email") String email);
}
