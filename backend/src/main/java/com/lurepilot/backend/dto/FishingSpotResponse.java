package com.lurepilot.backend.dto;

import java.time.Instant;

public record FishingSpotResponse(
        Long id,
        String name,
        String description,
        Double latitude,
        Double longitude,
        String waterType,
        String favoriteSpecies,
        Instant createdAt
) {
}
