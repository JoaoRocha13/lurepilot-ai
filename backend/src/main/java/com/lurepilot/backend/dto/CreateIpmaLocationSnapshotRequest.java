package com.lurepilot.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateIpmaLocationSnapshotRequest(
        @NotNull
        Integer globalIdLocal,

        LocalDate forecastDate
) {
}
