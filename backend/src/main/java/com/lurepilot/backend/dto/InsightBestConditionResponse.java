package com.lurepilot.backend.dto;

import java.time.LocalDate;

public record InsightBestConditionResponse(
        String waterClarity,
        String waterLevel,
        Integer weatherTypeId,
        String windDirection,
        Integer windSpeedClass,
        long totalSessions,
        long successfulSessions,
        double successRate,
        long totalFishCaught,
        Double averageRating,
        LocalDate lastSessionDate
) {
}
