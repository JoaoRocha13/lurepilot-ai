package com.lurepilot.backend.dto;

public record WeatherHourlyResponse(
        String time,
        Double temperature,
        Double relativeHumidity,
        Double precipitationProbability,
        Double precipitation,
        Integer weatherCode,
        Double windSpeedKmh,
        Integer windDirection,
        Double windGustsKmh
) {
}
