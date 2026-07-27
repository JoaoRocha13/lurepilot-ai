package com.lurepilot.backend.dto;

import java.time.Instant;
import java.util.List;

public record AiSessionAdjustmentResponse(
        Long id,
        Long sessionId,
        Long planId,
        Integer version,
        String summary,
        List<AiLureRankingResponse> lureRanking,
        String immediateAction,
        String nextTechnique,
        String fallbackAction,
        List<String> avoid,
        String confidence,
        Integer confidenceScore,
        String confidenceReason,
        Boolean latest,
        List<String> warnings,
        Instant createdAt
) {
}
