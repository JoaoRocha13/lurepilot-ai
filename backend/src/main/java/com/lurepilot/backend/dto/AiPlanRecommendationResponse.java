package com.lurepilot.backend.dto;

import java.time.Instant;
import java.util.List;

public record AiPlanRecommendationResponse(
        Long id,
        Long planId,
        String summary,
        List<AiLureRankingResponse> lureRanking,
        String planA,
        String planB,
        String planC,
        List<String> avoid,
        String confidence,
        List<String> warnings,
        Instant createdAt
) {
}
