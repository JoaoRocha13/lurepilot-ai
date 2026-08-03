package com.lurepilot.backend.dto;

public record FishSpeciesSummaryResponse(
        Long id,
        String name,
        String imageUrl,
        String waterEnvironment,
        String strikeZone,
        String favoriteLures
) {
    public FishSpeciesSummaryResponse(Long id, String name, String imageUrl, String strikeZone, String favoriteLures) {
        this(id, name, imageUrl, null, strikeZone, favoriteLures);
    }
}
