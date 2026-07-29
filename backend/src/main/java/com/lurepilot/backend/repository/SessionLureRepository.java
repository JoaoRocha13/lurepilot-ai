package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.SessionLure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessionLureRepository extends JpaRepository<SessionLure, Long> {

    List<SessionLure> findBySessionIdOrderByUsedFromAscIdAsc(Long sessionId);

    @Query("""
            select sl.lure.name, count(sl), sum(case when sl.session.success = true then 1 else 0 end)
            from SessionLure sl
            group by sl.lure.id, sl.lure.name
            order by count(sl) desc
            """)
    List<Object[]> summarizeLureUsage();
}
