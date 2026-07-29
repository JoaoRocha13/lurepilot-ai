package com.lurepilot.backend.dto;

import java.time.LocalDate;

public record InsightBestSpotResponse(
        Long spotId,
        String spotName,
        long totalSessions,
        long successfulSessions,
        double successRate,
        long totalFishCaught,
        LocalDate lastSessionDate
) {
}
