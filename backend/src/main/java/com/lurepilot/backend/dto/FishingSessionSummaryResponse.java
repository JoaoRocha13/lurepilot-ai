package com.lurepilot.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record FishingSessionSummaryResponse(
        Long id,
        Long spotId,
        String spotName,
        Long planId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        String targetSpecies,
        Boolean success,
        Integer rating
) {
}
