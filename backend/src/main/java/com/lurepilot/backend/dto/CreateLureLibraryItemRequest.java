package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLureLibraryItemRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @Size(max = 100)
        String type,

        @Size(max = 500)
        String imageUrl,

        @Size(max = 100)
        String difficulty,

        @Size(max = 100)
        String effectiveness,

        @Size(max = 1000)
        String description,

        @Size(max = 1000)
        String usageNotes,

        @Size(max = 500)
        String actionType,

        @Size(max = 1000)
        String idealConditions
) {
}
