package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record CreateSessionEventRequest(
        LocalTime eventTime,

        @NotBlank
        @Size(max = 100)
        String eventType,

        @Size(max = 1000)
        String description
) {
}
