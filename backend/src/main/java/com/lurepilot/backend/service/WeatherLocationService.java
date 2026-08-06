package com.lurepilot.backend.service;

import com.lurepilot.backend.client.OpenMeteoClient;
import com.lurepilot.backend.dto.WeatherLocationOptionResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class WeatherLocationService {

    private final OpenMeteoClient openMeteoClient;

    public WeatherLocationService(OpenMeteoClient openMeteoClient) {
        this.openMeteoClient = openMeteoClient;
    }

    public List<WeatherLocationOptionResponse> searchLocations(String query, String countryCode) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        return openMeteoClient.searchLocations(query.trim(), countryCode)
                .stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing(WeatherLocationOptionResponse::name))
                .toList();
    }

    private WeatherLocationOptionResponse toResponse(OpenMeteoClient.Location location) {
        return new WeatherLocationOptionResponse(
                location.id(),
                location.name(),
                location.latitude(),
                location.longitude(),
                location.elevation(),
                location.country(),
                location.admin1(),
                location.admin2(),
                location.timezone(),
                location.featureCode()
        );
    }
}
