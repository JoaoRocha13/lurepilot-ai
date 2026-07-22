package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateFishingSessionRequest(
        @NotNull
        Long spotId,

        Long planId,

        @NotNull
        LocalDate date,

        LocalTime startTime,

        LocalTime endTime,

        @NotBlank
        @Size(max = 100)
        String targetSpecies,

        @NotBlank
        @Size(max = 100)
        String waterClarity,

        @NotBlank
        @Size(max = 100)
        String waterLevel,

        @Size(max = 1000)
        String notes,

        Boolean success
) {
}
