package com.lurepilot.backend.dto;

public record FishingSpotSummaryResponse(
        Long id,
        String name,
        Double latitude,
        Double longitude,
        String waterType,
        String spotType,
        String favoriteSpecies
) {
}
