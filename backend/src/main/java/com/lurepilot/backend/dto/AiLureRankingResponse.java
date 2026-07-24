package com.lurepilot.backend.dto;

public record AiLureRankingResponse(
        Integer rank,
        String lure,
        String reason
) {
}
