package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.AiRecommendation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AiRecommendationRepository extends JpaRepository<AiRecommendation, Long> {

    @EntityGraph(attributePaths = {"plan", "session"})
    List<AiRecommendation> findByPlanIdOrderByCreatedAtDescIdDesc(Long planId);

    @EntityGraph(attributePaths = {"plan", "session"})
    List<AiRecommendation> findByPlanIdAndRecommendationTypeOrderByCreatedAtDescIdDesc(Long planId, String recommendationType);

    @EntityGraph(attributePaths = {"plan", "session"})
    Optional<AiRecommendation> findFirstByPlanIdAndRecommendationTypeOrderByVersionDescIdDesc(Long planId, String recommendationType);

    @Query("""
            select a from AiRecommendation a
            where a.plan.id = :planId
              and a.recommendationType = :recommendationType
              and (a.latest = true or a.latest is null)
            """)
    List<AiRecommendation> findLatestByPlanIdAndRecommendationType(@Param("planId") Long planId, @Param("recommendationType") String recommendationType);

    List<AiRecommendation> findBySessionIdAndRecommendationTypeOrderByCreatedAtDescIdDesc(Long sessionId, String recommendationType);

    Optional<AiRecommendation> findFirstBySessionIdAndRecommendationTypeOrderByVersionDescIdDesc(Long sessionId, String recommendationType);

    @Query("""
            select a from AiRecommendation a
            where a.session.id = :sessionId
              and a.recommendationType = :recommendationType
              and (a.latest = true or a.latest is null)
            """)
    List<AiRecommendation> findLatestBySessionIdAndRecommendationType(@Param("sessionId") Long sessionId, @Param("recommendationType") String recommendationType);

    long countByPlanIdAndRecommendationType(Long planId, String recommendationType);

    long countBySessionIdAndRecommendationType(Long sessionId, String recommendationType);

    @Query("""
            select a from AiRecommendation a
            where (a.latest = true or a.latest is null)
              and not exists (
                  select e.id from RecommendationExecution e
                  where e.recommendation.id = a.id
              )
            order by a.createdAt desc, a.id desc
            """)
    List<AiRecommendation> findPendingEvaluation(Pageable pageable);

    @Query("""
            select count(a) from AiRecommendation a
            where (a.latest = true or a.latest is null)
              and not exists (
                  select e.id from RecommendationExecution e
                  where e.recommendation.id = a.id
              )
            """)
    long countPendingEvaluation();
}
