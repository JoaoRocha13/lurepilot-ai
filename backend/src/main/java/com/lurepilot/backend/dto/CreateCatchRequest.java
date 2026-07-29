package com.lurepilot.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCatchRequest(
        @NotBlank
        @Size(max = 100)
        String species,

        @NotNull
        @Min(1)
        Integer quantity,

        Double sizeCm,

        Double weightKg,

        Boolean released,

        @Size(max = 1000)
        String notes,

        @Size(max = 1000)
        String photoUrl,

        @Size(max = 1000)
        String photoThumbnailUrl,

        @Size(max = 255)
        String photoCaption
) {
}
