package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFishSpeciesRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @jakarta.validation.constraints.Pattern(regexp = "(?i)FRESHWATER|SALTWATER")
        String waterEnvironment,

        @Size(max = 1000)
        String description,

        @Size(max = 5000000)
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
