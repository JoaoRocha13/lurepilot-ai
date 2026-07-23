package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFishSpeciesRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 1000)
        String description,

        @Size(max = 500)
        String imageUrl,

        @Size(max = 1000)
        String habitatNotes,

        @Size(max = 1000)
        String activeTimes,

        @Size(max = 500)
        String strikeZone,

        @Size(max = 1000)
        String commonZones,

        @Size(max = 1000)
        String favoriteLures
) {
}
