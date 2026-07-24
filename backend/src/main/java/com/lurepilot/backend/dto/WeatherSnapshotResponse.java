package com.lurepilot.backend.dto;

import java.time.Instant;
import java.time.LocalDate;

public record WeatherSnapshotResponse(
        Long id,
        Long planId,
        Long sessionId,
        String source,
        Integer sourceGlobalIdLocal,
        String sourceLocationName,
        Double sourceLatitude,
        Double sourceLongitude,
        LocalDate forecastDate,
        Instant dataUpdate,
        Integer weatherTypeId,
        Double temperatureMin,
        Double temperatureMax,
        Double precipitationProbability,
        String windDirection,
        Integer windSpeedClass,
        String notes,
        Instant capturedAt
) {
}
