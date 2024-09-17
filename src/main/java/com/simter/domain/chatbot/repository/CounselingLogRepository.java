package com.simter.domain.chatbot.repository;

import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CounselingLogRepository extends JpaRepository<CounselingLog, Long> {

    @Query("SELECT c FROM CounselingLog c WHERE c.user = :member AND DATE(c.startedAt) = :date")
    List<CounselingLog> findByUserAndDate(@Param("member") Member member, @Param("date") LocalDate date);

    List<CounselingLog> findByCalendars(Calendars calendars);

}
