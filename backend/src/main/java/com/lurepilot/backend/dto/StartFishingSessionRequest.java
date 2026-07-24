package com.lurepilot.backend.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record StartFishingSessionRequest(
        LocalTime startTime,

        @Size(max = 1000)
        String notes
) {
}
