package com.lurepilot.backend.dto;

public record AnalyticsRecommendationStepResponse(
        String recommendationStep,
        long totalExecutions,
        long followedExecutions,
        long successfulExecutions,
        double followRate,
        double successRate
) {
}
