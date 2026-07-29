package com.lurepilot.backend.dto;

import java.time.LocalDate;

public record InsightTopLureResponse(
        Long lureId,
        String lureName,
        String lureType,
        long timesUsed,
        long successfulSessions,
        double successRate,
        long totalFishCaught,
        LocalDate lastUsedDate
) {
}
