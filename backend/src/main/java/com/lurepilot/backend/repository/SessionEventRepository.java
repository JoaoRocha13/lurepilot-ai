package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.SessionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionEventRepository extends JpaRepository<SessionEvent, Long> {

    List<SessionEvent> findBySessionIdOrderByEventTimeAscIdAsc(Long sessionId);
}
