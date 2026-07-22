package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
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

        @Size(max = 255)
        String targetSpecies,

        @NotBlank
        @Size(max = 100)
        String waterType
) {
}
