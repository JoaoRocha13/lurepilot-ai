package com.lurepilot.backend.dto;

import java.time.Instant;
import java.util.List;

public record AiSessionReviewResponse(
        Long id,
        Long sessionId,
        Long planId,
        Integer version,
        String summary,
        String whatWorked,
        String whatFailed,
        String bestLure,
        String bestLureReason,
        String observedPattern,
        String nextSessionSuggestion,
        List<String> keyLessons,
        String confidence,
        List<String> warnings,
        Instant createdAt
) {
}
