package com.lurepilot.backend.dto;

import java.time.Instant;
import java.time.LocalDate;

public record DashboardWeatherSnapshotResponse(
        Long id,
        Long planId,
        Long sessionId,
        String sourceLocationName,
        LocalDate forecastDate,
        Double temperatureMin,
        Double temperatureMax,
        Double precipitationProbability,
        String windDirection,
        Integer windSpeedClass,
        Double currentTemperature,
        Double relativeHumidity,
        Double windSpeedKmh,
        Double windGustsKmh,
        Double pressureMsl,
        Instant capturedAt
) {
}
