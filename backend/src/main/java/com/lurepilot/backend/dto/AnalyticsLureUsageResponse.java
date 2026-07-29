package com.lurepilot.backend.dto;

public record AnalyticsLureUsageResponse(
        String lureName,
        long timesUsed,
        long successfulSessions,
        double successRate
) {
}
