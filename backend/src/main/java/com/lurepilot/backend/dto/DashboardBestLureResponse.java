package com.lurepilot.backend.dto;

import java.time.LocalDate;

public record DashboardBestLureResponse(
        Long lureId,
        String lureName,
        String lureType,
        long uses,
        long successfulSessions,
        double successRate,
        LocalDate lastUsedDate
) {
}
