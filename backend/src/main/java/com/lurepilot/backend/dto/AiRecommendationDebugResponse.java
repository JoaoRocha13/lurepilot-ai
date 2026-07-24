package com.lurepilot.backend.dto;

import java.time.Instant;

public record AiRecommendationDebugResponse(
        Long id,
        Long planId,
        Long sessionId,
        String recommendationType,
        Integer version,
        String contextJson,
        String rawResponse,
        Instant createdAt
) {
}
