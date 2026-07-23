package com.lurepilot.backend.dto;

import java.util.List;

public record DashboardResponse(
        long totalSpots,
        long totalFishSpecies,
        long totalLures,
        long totalPlans,
        long totalSessions,
        long successfulSessions,
        long totalCatchRecords,
        long totalFishCaught,
        List<DashboardRecentSessionResponse> recentSessions
) {
}
