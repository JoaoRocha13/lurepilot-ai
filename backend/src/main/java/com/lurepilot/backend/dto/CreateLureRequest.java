package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CreateLureRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @Size(max = 100)
        String type,

        @Size(max = 100)
        String color,

        @Size(max = 100)
        String size,

        Double weight,

        @Size(max = 255)
        String brand,

        @Size(max = 1000)
        String notes,

        Long libraryItemId,

        @Size(max = 255)
        String targetSpecies,

        @NotBlank
        @Size(max = 100)
        String waterType,

        Boolean active,

        @Min(0)
        Integer quantity,

        @Size(max = 100)
        String condition,

        @Size(max = 1000)
        String personalNotes,

        @Size(max = 255)
        String favoriteForSpecies,

        @Size(max = 255)
        String favoriteForSpot
) {
}
