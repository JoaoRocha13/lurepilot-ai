package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.RecommendationExecution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
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

    @Query("""
            select e.recommendationType,
                   e.recommendationStep,
                   count(e),
                   sum(case when e.followed = true then 1 else 0 end),
                   sum(case when e.success = true then 1 else 0 end),
                   avg(e.rating),
                   max(e.createdAt)
            from RecommendationExecution e
            where (:dateFrom is null or e.createdAt >= :dateFrom)
              and (:dateToExclusive is null or e.createdAt < :dateToExclusive)
              and (:recommendationType is null or lower(e.recommendationType) = lower(:recommendationType))
              and (:species is null or (
                  (e.session is not null and lower(e.session.targetSpecies) = lower(:species))
                  or (e.session is null and e.plan is not null and lower(e.plan.targetSpecies) = lower(:species))
              ))
              and (:spotId is null or (
                  (e.session is not null and e.session.spot.id = :spotId)
                  or (e.session is null and e.plan is not null and e.plan.spot.id = :spotId)
              ))
              and (:lureId is null or (
                  exists (
                      select sl.id from SessionLure sl
                      where e.session is not null
                        and sl.session = e.session
                        and sl.lure.id = :lureId
                  )
                  or exists (
                      select fpl.id from FishingPlanLure fpl
                      where e.plan is not null
                        and fpl.plan = e.plan
                        and fpl.lure.id = :lureId
                  )
              ))
            group by e.recommendationType, e.recommendationStep
            order by sum(case when e.success = true then 1 else 0 end) desc,
                     count(e) desc,
                     avg(e.rating) desc
            """)
    List<Object[]> findRecommendationPerformanceInsights(
            @Param("dateFrom") Instant dateFrom,
            @Param("dateToExclusive") Instant dateToExclusive,
            @Param("species") String species,
            @Param("spotId") Long spotId,
            @Param("lureId") Long lureId,
            @Param("recommendationType") String recommendationType,
            Pageable pageable
    );
}
