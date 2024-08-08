package com.simter.domain.mail.repository;


import com.simter.domain.mail.entity.Mail;
import com.simter.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {

    // 특정 사용자의 삭제되지 않은 메일 조회
    List<Mail> findByMemberAndIsDeletedFalse(Member member);

    // 특정 사용자의 즐겨찾기 한 메일 조회
    List<Mail> findByMemberAndIsStaredTrue(Member member);

    Optional<Mail> findById(Long id);

}
