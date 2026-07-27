package com.lurepilot.backend.dto;

import java.util.List;

public record DashboardResponse(
        long totalSpots,
        long totalFishSpecies,
        long totalLures,
        long totalLureLibraryItems,
        long totalPlans,
        long totalSessions,
        long successfulSessions,
        long totalCatchRecords,
        long totalFishCaught,
        List<DashboardUpcomingPlanResponse> upcomingPlans,
        List<DashboardActiveSessionResponse> activeSessions,
        List<DashboardRecentSessionResponse> recentSessions,
        List<DashboardRecentCatchResponse> recentCatches,
        List<DashboardWeatherSnapshotResponse> recentWeatherSnapshots
) {
}
