package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.WeatherLocationOptionResponse;
import com.lurepilot.backend.service.WeatherLocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather-locations")
public class WeatherLocationController {

    private final WeatherLocationService weatherLocationService;

    public WeatherLocationController(WeatherLocationService weatherLocationService) {
        this.weatherLocationService = weatherLocationService;
    }

    @GetMapping("/search")
    public List<WeatherLocationOptionResponse> searchLocations(
            @RequestParam String query,
            @RequestParam(defaultValue = "PT") String countryCode
    ) {
        return weatherLocationService.searchLocations(query, countryCode);
    }
}
