package com.lurepilot.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record CreateRecommendationExecutionRequest(
        Long sessionId,

        @NotBlank
        @Size(max = 100)
        String recommendationStep,

        @NotNull
        Boolean followed,

        @NotBlank
        @Size(max = 255)
        String result,

        Boolean success,

        @Min(1)
        @Max(5)
        Integer rating,

        LocalTime startedAt,

        LocalTime endedAt,

        @Size(max = 1000)
        String notes
) {
}
