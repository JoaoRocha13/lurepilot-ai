package com.lurepilot.backend.dto;

public record WeatherLocationOptionResponse(
        Integer id,
        String name,
        Double latitude,
        Double longitude,
        Double elevation,
        String country,
        String admin1,
        String admin2,
        String timezone,
        String featureCode
) {
}
