package com.lurepilot.backend.dto;

public record SolunarPeriodResponse(
        String type,
        String title,
        String peakAt,
        String startsAt,
        String endsAt
) {
}
