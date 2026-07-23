package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record AddSessionLureRequest(
        @NotNull
        Long lureId,

        LocalTime usedFrom,

        LocalTime usedTo,

        @Size(max = 1000)
        String resultNotes
) {
}
