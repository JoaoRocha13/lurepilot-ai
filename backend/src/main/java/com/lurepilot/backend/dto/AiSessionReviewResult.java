package com.lurepilot.backend.dto;

import java.util.List;

public record AiSessionReviewResult(
        String summary,
        String whatWorked,
        String whatFailed,
        String bestLure,
        String bestLureReason,
        String observedPattern,
        String nextSessionSuggestion,
        List<String> keyLessons,
        String confidence,
        List<String> warnings
) {
}
