package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateWeatherCoordinateSnapshotRequest;
import com.lurepilot.backend.dto.CreateWeatherLocationSnapshotRequest;
import com.lurepilot.backend.dto.WeatherSnapshotResponse;
import com.lurepilot.backend.service.WeatherSnapshotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather-snapshots")
public class WeatherSnapshotController {

    private final WeatherSnapshotService weatherSnapshotService;

    public WeatherSnapshotController(WeatherSnapshotService weatherSnapshotService) {
        this.weatherSnapshotService = weatherSnapshotService;
    }

    @PostMapping("/plans/{planId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherSnapshotResponse createSnapshotForPlan(@PathVariable Long planId) {
        return weatherSnapshotService.createSnapshotForPlan(planId);
    }

    @PostMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherSnapshotResponse createSnapshotForSession(@PathVariable Long sessionId) {
        return weatherSnapshotService.createSnapshotForSession(sessionId);
    }

    @PostMapping("/coordinates")
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherSnapshotResponse createSnapshotForCoordinates(@Valid @RequestBody CreateWeatherCoordinateSnapshotRequest request) {
        return weatherSnapshotService.createSnapshotForCoordinates(request);
    }

    @PostMapping("/location")
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherSnapshotResponse createSnapshotForLocation(@Valid @RequestBody CreateWeatherLocationSnapshotRequest request) {
        return weatherSnapshotService.createSnapshotForLocation(request);
    }

    @GetMapping("/plans/{planId}")
    public List<WeatherSnapshotResponse> getSnapshotsByPlan(@PathVariable Long planId) {
        return weatherSnapshotService.getSnapshotsByPlan(planId);
    }

    @GetMapping("/sessions/{sessionId}")
    public List<WeatherSnapshotResponse> getSnapshotsBySession(@PathVariable Long sessionId) {
        return weatherSnapshotService.getSnapshotsBySession(sessionId);
    }

    @GetMapping("/plans/{planId}/latest")
    public WeatherSnapshotResponse getLatestSnapshotForPlan(@PathVariable Long planId) {
        return weatherSnapshotService.getLatestSnapshotForPlan(planId);
    }

    @GetMapping("/sessions/{sessionId}/latest")
    public WeatherSnapshotResponse getLatestSnapshotForSession(@PathVariable Long sessionId) {
        return weatherSnapshotService.getLatestSnapshotForSession(sessionId);
    }
}
