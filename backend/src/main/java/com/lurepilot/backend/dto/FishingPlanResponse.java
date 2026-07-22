package com.lurepilot.backend.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record FishingPlanResponse(
        Long id,
        Long spotId,
        String spotName,
        LocalDate plannedDate,
        LocalTime plannedTime,
        String targetSpecies,
        String waterClarity,
        String waterLevel,
        String notes,
        Instant createdAt
) {
}
