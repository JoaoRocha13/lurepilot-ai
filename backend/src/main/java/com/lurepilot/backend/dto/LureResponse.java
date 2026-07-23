package com.lurepilot.backend.dto;

import java.time.Instant;

public record LureResponse(
        Long id,
        String name,
        String type,
        String color,
        String size,
        Double weight,
        String brand,
        String notes,
        Long libraryItemId,
        String libraryItemName,
        String targetSpecies,
        String waterType,
        Instant createdAt
) {
}
