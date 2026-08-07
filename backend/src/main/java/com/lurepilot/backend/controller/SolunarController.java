package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.SolunarForecastResponse;
import com.lurepilot.backend.service.SolunarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/solunar")
public class SolunarController {

    private final SolunarService solunarService;

    public SolunarController(SolunarService solunarService) {
        this.solunarService = solunarService;
    }

    @GetMapping("/spots/{spotId}")
    public SolunarForecastResponse getForecast(
            @PathVariable Long spotId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return solunarService.getForecast(spotId, date);
    }

    @GetMapping("/coordinates")
    public SolunarForecastResponse getForecastForCoordinates(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) String locationName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return solunarService.getForecast(latitude, longitude, locationName, date);
    }
}
