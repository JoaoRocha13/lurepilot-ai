package com.lurepilot.backend.dto;

import java.time.Instant;

public record DashboardPendingRecommendationResponse(
        Long id,
        Long planId,
        Long sessionId,
        String recommendationType,
        Integer version,
        String summary,
        String confidence,
        Integer confidenceScore,
        Instant createdAt
) {
}
