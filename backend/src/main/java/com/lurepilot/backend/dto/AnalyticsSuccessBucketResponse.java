package com.lurepilot.backend.dto;

public record AnalyticsSuccessBucketResponse(
        String label,
        long totalSessions,
        long successfulSessions,
        double successRate
) {
}
