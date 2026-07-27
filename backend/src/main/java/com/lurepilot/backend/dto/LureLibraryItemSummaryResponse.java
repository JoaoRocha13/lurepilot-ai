package com.lurepilot.backend.dto;

public record LureLibraryItemSummaryResponse(
        Long id,
        String name,
        String type,
        String imageUrl,
        String difficulty,
        String effectiveness,
        String actionType
) {
}
