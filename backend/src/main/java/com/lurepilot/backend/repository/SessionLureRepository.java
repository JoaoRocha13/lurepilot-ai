package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.SessionLure;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SessionLureRepository extends JpaRepository<SessionLure, Long> {

    List<SessionLure> findBySessionIdOrderByUsedFromAscIdAsc(Long sessionId);

    @Query("""
            select sl.lure.id, sl.lure.name, sl.lure.type, count(sl), sum(case when sl.session.success = true then 1 else 0 end), max(sl.session.date)
            from SessionLure sl
            where sl.session.date >= :dateFrom
            group by sl.lure.id, sl.lure.name, sl.lure.type
            order by sum(case when sl.session.success = true then 1 else 0 end) desc, count(sl) desc, max(sl.session.date) desc
            """)
    List<Object[]> findBestRecentLures(@Param("dateFrom") LocalDate dateFrom, Pageable pageable);

    @Query("""
            select sl.lure.name, count(sl), sum(case when sl.session.success = true then 1 else 0 end)
            from SessionLure sl
            group by sl.lure.id, sl.lure.name
            order by count(sl) desc
            """)
    List<Object[]> summarizeLureUsage();
}
