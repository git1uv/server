package com.simter.domain.mail.repository;


import com.simter.domain.mail.entity.Mail;
import com.simter.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {

    // 특정 사용자의 삭제되지 않은 편지 조회
    List<Mail> findByMemberAndIsDeletedFalseAndCreatedAtBefore(Member member, LocalDateTime currentDateTime);

    // 특정 사용자의 즐겨찾기 한 편지 조회
    List<Mail> findByMemberAndIsStarredTrueAndCreatedAtBefore(Member member, LocalDateTime currentDateTime);


    Optional<Mail> findById(Long id);

    Optional<Mail> findByIdAndIsDeletedFalseAndCreatedAtBefore(Long id, LocalDateTime currentDateTime);

    // 삭제되지 않고 즐겨찾기된 메일 조회
    List<Mail> findByMemberAndIsDeletedFalseAndIsStarredTrueAndCreatedAtBefore(Member member, LocalDateTime currentDateTime);

    // 삭제되지 않고 읽지 않은 메일 조회
    List<Mail> findByMemberAndIsDeletedFalseAndIsReadFalseAndCreatedAtBefore(Member member, LocalDateTime currentDateTime);

    long countByMemberAndIsDeletedFalseAndIsReadFalseAndCreatedAtBefore(Member member, LocalDateTime currentDateTime);

}
