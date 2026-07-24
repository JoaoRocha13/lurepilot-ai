package com.lurepilot.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record FinishFishingSessionRequest(
        LocalTime endTime,

        Boolean success,

        @Size(max = 1000)
        String resultSummary,

        @Size(max = 1000)
        String finalNotes,

        @Min(1)
        @Max(5)
        Integer rating
) {
}
