package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateIpmaCoordinateSnapshotRequest;
import com.lurepilot.backend.dto.CreateIpmaLocationSnapshotRequest;
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

    @PostMapping("/plans/{planId}/ipma")
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherSnapshotResponse createIpmaSnapshotForPlan(@PathVariable Long planId) {
        return weatherSnapshotService.createIpmaSnapshotForPlan(planId);
    }

    @PostMapping("/sessions/{sessionId}/ipma")
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherSnapshotResponse createIpmaSnapshotForSession(@PathVariable Long sessionId) {
        return weatherSnapshotService.createIpmaSnapshotForSession(sessionId);
    }

    @PostMapping("/ipma/coordinates")
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherSnapshotResponse createIpmaSnapshotForCoordinates(@Valid @RequestBody CreateIpmaCoordinateSnapshotRequest request) {
        return weatherSnapshotService.createIpmaSnapshotForCoordinates(request);
    }

    @PostMapping("/ipma/location")
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherSnapshotResponse createIpmaSnapshotForLocation(@Valid @RequestBody CreateIpmaLocationSnapshotRequest request) {
        return weatherSnapshotService.createIpmaSnapshotForLocation(request);
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
