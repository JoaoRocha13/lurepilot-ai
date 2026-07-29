package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.RecommendationExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecommendationExecutionRepository extends JpaRepository<RecommendationExecution, Long> {

    List<RecommendationExecution> findByRecommendationIdOrderByCreatedAtDescIdDesc(Long recommendationId);

    List<RecommendationExecution> findByPlanIdOrderByCreatedAtDescIdDesc(Long planId);

    List<RecommendationExecution> findBySessionIdOrderByCreatedAtDescIdDesc(Long sessionId);

    long countByFollowedTrue();

    long countBySuccessTrue();

    @Query("""
            select e.recommendationStep, count(e), sum(case when e.followed = true then 1 else 0 end), sum(case when e.success = true then 1 else 0 end)
            from RecommendationExecution e
            group by e.recommendationStep
            order by count(e) desc
            """)
    List<Object[]> summarizeByRecommendationStep();
}
