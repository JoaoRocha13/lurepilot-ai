package com.lurepilot.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DashboardRecentSessionResponse(
        Long id,
        Long spotId,
        String spotName,
        LocalDate date,
        LocalTime startTime,
        String status,
        String targetSpecies,
        Boolean success
) {
}
