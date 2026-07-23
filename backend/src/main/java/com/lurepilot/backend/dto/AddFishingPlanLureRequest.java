package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotNull;

public record AddFishingPlanLureRequest(
        @NotNull
        Long lureId
) {
}
