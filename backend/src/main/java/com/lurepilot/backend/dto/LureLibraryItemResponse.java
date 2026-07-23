package com.lurepilot.backend.dto;

import java.time.Instant;

public record LureLibraryItemResponse(
        Long id,
        String name,
        String type,
        String imageUrl,
        String difficulty,
        String effectiveness,
        String description,
        String usageNotes,
        String actionType,
        String idealConditions,
        Instant createdAt
) {
}
