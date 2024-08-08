package com.simter.domain.member.repository;

import com.simter.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    List<Member> findAllByHasAirplane(Boolean hasAirplane);

    Optional<Member> findById(Long id);

}
