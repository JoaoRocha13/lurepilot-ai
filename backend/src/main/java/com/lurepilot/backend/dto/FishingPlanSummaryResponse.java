package com.lurepilot.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record FishingPlanSummaryResponse(
        Long id,
        Long spotId,
        String spotName,
        LocalDate plannedDate,
        LocalTime plannedTime,
        String targetSpecies,
        String waterClarity,
        String waterLevel
) {
}
