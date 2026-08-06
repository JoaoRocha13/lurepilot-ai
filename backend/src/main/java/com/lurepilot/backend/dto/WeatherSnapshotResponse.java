package com.lurepilot.backend.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record WeatherSnapshotResponse(
        Long id,
        Long planId,
        Long sessionId,
        String source,
        Integer sourceLocationId,
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
        Double currentTemperature,
        Double apparentTemperature,
        Double relativeHumidity,
        Double precipitation,
        Double pressureMsl,
        Integer cloudCover,
        Double windSpeedKmh,
        Double windGustsKmh,
        String sunrise,
        String sunset,
        List<WeatherHourlyResponse> hourlyForecast,
        String notes,
        Instant capturedAt
) {
}
