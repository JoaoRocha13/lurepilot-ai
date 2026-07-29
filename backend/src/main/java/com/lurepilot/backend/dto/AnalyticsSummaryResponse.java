package com.lurepilot.backend.dto;

import java.util.List;

public record AnalyticsSummaryResponse(
        long totalSessions,
        long finishedSessions,
        long successfulSessions,
        double sessionSuccessRate,
        long totalCatchRecords,
        long totalFishCaught,
        Double averageSessionRating,
        long totalRecommendationExecutions,
        long followedRecommendationExecutions,
        long successfulRecommendationExecutions,
        double recommendationFollowRate,
        double recommendationSuccessRate,
        List<AnalyticsSuccessBucketResponse> successBySpecies,
        List<AnalyticsSuccessBucketResponse> successBySpot,
        List<AnalyticsCatchBucketResponse> catchesBySpecies,
        List<AnalyticsLureUsageResponse> lureUsage,
        List<AnalyticsRecommendationStepResponse> recommendationSteps
) {
}
