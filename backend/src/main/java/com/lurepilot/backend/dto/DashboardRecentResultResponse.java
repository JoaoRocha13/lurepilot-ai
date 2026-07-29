package com.lurepilot.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DashboardRecentResultResponse(
        Long sessionId,
        Long spotId,
        String spotName,
        LocalDate date,
        LocalTime startTime,
        String targetSpecies,
        Boolean success,
        Long totalFishCaught,
        String resultSummary,
        Integer rating
) {
}
