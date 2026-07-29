package com.lurepilot.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DashboardNextPlannedSessionResponse(
        String sourceType,
        Long sessionId,
        Long planId,
        Long spotId,
        String spotName,
        LocalDate date,
        LocalTime time,
        String targetSpecies,
        String waterClarity,
        String waterLevel,
        String notes
) {
}
