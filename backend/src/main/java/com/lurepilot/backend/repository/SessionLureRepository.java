package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.SessionLure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionLureRepository extends JpaRepository<SessionLure, Long> {

    List<SessionLure> findBySessionIdOrderByUsedFromAscIdAsc(Long sessionId);
}
