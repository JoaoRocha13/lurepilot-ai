package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

        @NotBlank
        @Size(max = 50)
        @Pattern(regexp = "RESERVOIR|RIVER|LAKE|ESTUARY|COAST|HARBOR", message = "spotType must be RESERVOIR, RIVER, LAKE, ESTUARY, COAST or HARBOR")
        String spotType,

        @Size(max = 255)
        String favoriteSpecies
) {
}
