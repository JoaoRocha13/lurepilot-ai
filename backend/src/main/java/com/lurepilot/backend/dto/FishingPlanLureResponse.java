package com.lurepilot.backend.dto;

public record FishingPlanLureResponse(
        Long id,
        Long planId,
        Long lureId,
        String lureName,
        String lureType,
        String color,
        Long libraryItemId,
        String libraryItemName
) {
}
