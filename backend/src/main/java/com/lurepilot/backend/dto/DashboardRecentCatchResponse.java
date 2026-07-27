package com.lurepilot.backend.dto;

public record DashboardRecentCatchResponse(
        Long id,
        Long sessionId,
        Long spotId,
        String spotName,
        String species,
        Integer quantity,
        Double sizeCm,
        Double weightKg,
        Boolean released
) {
}
