package com.lurepilot.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CatchGalleryItemResponse(
        Long catchId,
        Long sessionId,
        LocalDate sessionDate,
        LocalTime sessionStartTime,
        Long spotId,
        String spotName,
        String species,
        Integer quantity,
        Double sizeCm,
        Double weightKg,
        Boolean released,
        String photoUrl,
        String photoThumbnailUrl,
        String photoCaption,
        Boolean sessionSuccess,
        Integer sessionRating
) {
}
