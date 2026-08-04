package com.lurepilot.backend.dto;

import java.time.Instant;

public record LureResponse(
        Long id,
        String name,
        String type,
        String imageUrl,
        String color,
        String size,
        Double weight,
        String brand,
        String notes,
        Long libraryItemId,
        String libraryItemName,
        String targetSpecies,
        String waterType,
        Boolean active,
        Integer quantity,
        String condition,
        String personalNotes,
        String favoriteForSpecies,
        String favoriteForSpot,
        Instant createdAt
) {
}
