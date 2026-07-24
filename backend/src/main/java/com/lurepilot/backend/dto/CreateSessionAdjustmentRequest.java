package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSessionAdjustmentRequest(
        @NotNull
        Long sessionId,

        @NotBlank
        @Size(max = 1000)
        String situation,

        @Size(max = 1000)
        String currentConditions
) {
}
