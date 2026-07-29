package com.lurepilot.backend.dto;

import java.time.Instant;

public record InsightRecommendationPerformanceResponse(
        String recommendationType,
        String recommendationStep,
        long totalExecutions,
        long followedExecutions,
        long successfulExecutions,
        double followRate,
        double successRate,
        Double averageRating,
        Instant lastExecutionAt
) {
}
