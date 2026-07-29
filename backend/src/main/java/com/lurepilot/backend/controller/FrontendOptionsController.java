package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.FrontendOptionsResponse;
import com.lurepilot.backend.dto.OptionResponse;
import com.lurepilot.backend.service.FrontendOptionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class FrontendOptionsController {

    private final FrontendOptionsService frontendOptionsService;

    public FrontendOptionsController(FrontendOptionsService frontendOptionsService) {
        this.frontendOptionsService = frontendOptionsService;
    }

    @GetMapping
    public FrontendOptionsResponse getAllOptions() {
        return frontendOptionsService.getAllOptions();
    }

    @GetMapping("/water-types")
    public List<OptionResponse> getWaterTypes() {
        return frontendOptionsService.getWaterTypes();
    }

    @GetMapping("/water-clarities")
    public List<OptionResponse> getWaterClarities() {
        return frontendOptionsService.getWaterClarities();
    }

    @GetMapping("/water-levels")
    public List<OptionResponse> getWaterLevels() {
        return frontendOptionsService.getWaterLevels();
    }

    @GetMapping("/lure-types")
    public List<OptionResponse> getLureTypes() {
        return frontendOptionsService.getLureTypes();
    }

    @GetMapping("/lure-difficulties")
    public List<OptionResponse> getLureDifficulties() {
        return frontendOptionsService.getLureDifficulties();
    }

    @GetMapping("/lure-effectiveness-levels")
    public List<OptionResponse> getLureEffectivenessLevels() {
        return frontendOptionsService.getLureEffectivenessLevels();
    }

    @GetMapping("/lure-conditions")
    public List<OptionResponse> getLureConditions() {
        return frontendOptionsService.getLureConditions();
    }

    @GetMapping("/fish-strike-zones")
    public List<OptionResponse> getFishStrikeZones() {
        return frontendOptionsService.getFishStrikeZones();
    }

    @GetMapping("/session-statuses")
    public List<OptionResponse> getSessionStatuses() {
        return frontendOptionsService.getSessionStatuses();
    }

    @GetMapping("/session-event-types")
    public List<OptionResponse> getSessionEventTypes() {
        return frontendOptionsService.getSessionEventTypes();
    }

    @GetMapping("/recommendation-types")
    public List<OptionResponse> getRecommendationTypes() {
        return frontendOptionsService.getRecommendationTypes();
    }

    @GetMapping("/recommendation-steps")
    public List<OptionResponse> getRecommendationSteps() {
        return frontendOptionsService.getRecommendationSteps();
    }

    @GetMapping("/recommendation-results")
    public List<OptionResponse> getRecommendationResults() {
        return frontendOptionsService.getRecommendationResults();
    }

    @GetMapping("/sort-directions")
    public List<OptionResponse> getSortDirections() {
        return frontendOptionsService.getSortDirections();
    }
}
