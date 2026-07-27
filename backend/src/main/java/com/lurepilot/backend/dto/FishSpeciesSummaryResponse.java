package com.lurepilot.backend.dto;

public record FishSpeciesSummaryResponse(
        Long id,
        String name,
        String imageUrl,
        String strikeZone,
        String favoriteLures
) {
}
