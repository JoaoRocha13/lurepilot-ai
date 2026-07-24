package com.lurepilot.backend.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record FishingSessionResponse(
        Long id,
        Long spotId,
        String spotName,
        Long planId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        String targetSpecies,
        String waterClarity,
        String waterLevel,
        String notes,
        Boolean success,
        Long durationMinutes,
        String resultSummary,
        String finalNotes,
        Integer rating,
        Instant createdAt
) {
}
