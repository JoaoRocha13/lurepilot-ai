package com.lurepilot.backend.dto;

import java.time.Instant;

public record FishSpeciesResponse(
        Long id,
        String name,
        String waterEnvironment,
        String description,
        String imageUrl,
        String habitatNotes,
        String activeTimes,
        String strikeZone,
        String commonZones,
        String favoriteLures,
        Instant createdAt
) {
}
