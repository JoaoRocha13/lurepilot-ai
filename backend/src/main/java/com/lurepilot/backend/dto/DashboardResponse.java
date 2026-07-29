package com.lurepilot.backend.dto;

import java.util.List;

public record DashboardResponse(
        long totalSpots,
        long totalFishSpecies,
        long totalLures,
        long totalLureLibraryItems,
        long totalPlans,
        long totalSessions,
        long finishedSessions,
        long successfulSessions,
        double successRate,
        long totalCatchRecords,
        long totalFishCaught,
        DashboardNextPlannedSessionResponse nextPlannedSession,
        DashboardBestLureResponse bestRecentLure,
        DashboardWeatherSnapshotResponse relevantWeatherSnapshot,
        long pendingRecommendationEvaluations,
        List<DashboardUpcomingPlanResponse> upcomingPlans,
        List<DashboardActiveSessionResponse> activeSessions,
        List<DashboardRecentResultResponse> recentResults,
        List<DashboardRecentSessionResponse> recentSessions,
        List<DashboardRecentCatchResponse> recentCatches,
        List<DashboardPendingRecommendationResponse> pendingRecommendations,
        List<DashboardWeatherSnapshotResponse> recentWeatherSnapshots
) {
}
