package com.lurepilot.backend.dto;

public record LureBoxItemSummaryResponse(
        Long id,
        String name,
        String type,
        String color,
        String size,
        String brand,
        Long libraryItemId,
        String libraryItemName,
        String targetSpecies,
        String waterType,
        Boolean active,
        Integer quantity,
        String condition
) {
}
