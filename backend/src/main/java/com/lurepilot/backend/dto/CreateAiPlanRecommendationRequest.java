package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAiPlanRecommendationRequest(
        @NotNull
        Long planId
) {
}
