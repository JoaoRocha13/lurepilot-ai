package com.lurepilot.backend.dto;

public record CatchResponse(
        Long id,
        Long sessionId,
        String species,
        Integer quantity,
        Double sizeCm,
        Double weightKg,
        Boolean released,
        String notes,
        String photoUrl,
        String photoThumbnailUrl,
        String photoCaption
) {
}
