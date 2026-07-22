package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFishingSpotRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 1000)
        String description,

        @NotNull
        Double latitude,

        @NotNull
        Double longitude,

        @NotBlank
        @Size(max = 100)
        String waterType,

        @Size(max = 255)
        String favoriteSpecies
) {
}
