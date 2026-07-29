package com.lurepilot.backend.dto;

import java.time.Instant;
import java.time.LocalTime;

public record RecommendationExecutionResponse(
        Long id,
        Long recommendationId,
        Long planId,
        Long sessionId,
        String recommendationType,
        Integer recommendationVersion,
        String recommendationStep,
        Boolean followed,
        String result,
        Boolean success,
        Integer rating,
        LocalTime startedAt,
        LocalTime endedAt,
        String notes,
        Instant createdAt
) {
}
