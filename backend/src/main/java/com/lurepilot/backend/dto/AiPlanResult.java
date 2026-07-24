package com.lurepilot.backend.dto;

import java.util.List;

public record AiPlanResult(
        String summary,
        List<AiLureRankingResponse> lureRanking,
        String planA,
        String planB,
        String planC,
        List<String> avoid,
        String confidence,
        List<String> warnings
) {
}
