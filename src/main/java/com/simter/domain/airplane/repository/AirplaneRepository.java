package com.simter.domain.airplane.repository;

import com.simter.domain.airplane.entity.Airplane;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AirplaneRepository extends JpaRepository<Airplane, Long> {
    Optional<Airplane> findFirstByReceiverId_IdOrderByCreatedAtDesc(Long receiverId);

}
