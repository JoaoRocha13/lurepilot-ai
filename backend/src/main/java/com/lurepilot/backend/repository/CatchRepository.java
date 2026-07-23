package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.Catch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatchRepository extends JpaRepository<Catch, Long> {

    List<Catch> findBySessionIdOrderByIdAsc(Long sessionId);
}
