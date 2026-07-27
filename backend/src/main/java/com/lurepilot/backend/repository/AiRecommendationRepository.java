package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.AiRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiRecommendationRepository extends JpaRepository<AiRecommendation, Long> {

    List<AiRecommendation> findByPlanIdOrderByCreatedAtDescIdDesc(Long planId);

    List<AiRecommendation> findBySessionIdAndRecommendationTypeOrderByCreatedAtDescIdDesc(Long sessionId, String recommendationType);

    long countByPlanIdAndRecommendationType(Long planId, String recommendationType);

    long countBySessionIdAndRecommendationType(Long sessionId, String recommendationType);
}
