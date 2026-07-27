package com.lurepilot.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DashboardActiveSessionResponse(
        Long id,
        Long spotId,
        String spotName,
        Long planId,
        LocalDate date,
        LocalTime startTime,
        String targetSpecies,
        String notes
) {
}
