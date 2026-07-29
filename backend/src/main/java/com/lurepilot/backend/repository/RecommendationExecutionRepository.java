package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.RecommendationExecution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendationExecutionRepository extends JpaRepository<RecommendationExecution, Long> {

    List<RecommendationExecution> findByRecommendationIdOrderByCreatedAtDescIdDesc(Long recommendationId);

    List<RecommendationExecution> findByPlanIdOrderByCreatedAtDescIdDesc(Long planId);

    List<RecommendationExecution> findBySessionIdOrderByCreatedAtDescIdDesc(Long sessionId);

    @Query("""
            select e from RecommendationExecution e
            where e.plan.id = :planId
              and e.recommendationType = :recommendationType
            order by e.createdAt desc, e.id desc
            """)
    List<RecommendationExecution> findRecentByPlanAndType(
            @Param("planId") Long planId,
            @Param("recommendationType") String recommendationType,
            Pageable pageable
    );

    @Query("""
            select e from RecommendationExecution e
            where e.plan is not null
              and e.plan.spot.id = :spotId
              and lower(e.plan.targetSpecies) = lower(:targetSpecies)
              and e.recommendationType = :recommendationType
            order by e.createdAt desc, e.id desc
            """)
    List<RecommendationExecution> findRecentBySpotSpeciesAndType(
            @Param("spotId") Long spotId,
            @Param("targetSpecies") String targetSpecies,
            @Param("recommendationType") String recommendationType,
            Pageable pageable
    );

    @Query("""
            select e from RecommendationExecution e
            where e.session.id = :sessionId
              and e.recommendationType = :recommendationType
            order by e.createdAt desc, e.id desc
            """)
    List<RecommendationExecution> findRecentBySessionAndType(
            @Param("sessionId") Long sessionId,
            @Param("recommendationType") String recommendationType,
            Pageable pageable
    );

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
