package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.Catch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CatchRepository extends JpaRepository<Catch, Long> {

    List<Catch> findBySessionIdOrderByIdAsc(Long sessionId);

    List<Catch> findTop5ByOrderByIdDesc();

    @Query("select coalesce(sum(c.quantity), 0) from Catch c")
    long sumTotalQuantity();

    @Query("""
            select c.species, coalesce(sum(c.quantity), 0)
            from Catch c
            group by c.species
            order by coalesce(sum(c.quantity), 0) desc
            """)
    List<Object[]> summarizeCatchQuantityBySpecies();
}
