package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotNull;

public record CreateSessionReviewRequest(
        @NotNull
        Long sessionId
) {
}
