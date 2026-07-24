package com.lurepilot.backend.dto;

import java.util.List;

public record AiSessionAdjustmentResult(
        String summary,
        List<AiLureRankingResponse> lureRanking,
        String immediateAction,
        String nextTechnique,
        String fallbackAction,
        List<String> avoid,
        String confidence,
        List<String> warnings
) {
}
