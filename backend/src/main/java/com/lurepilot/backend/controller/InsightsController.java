package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.InsightBestConditionResponse;
import com.lurepilot.backend.dto.InsightBestSpotResponse;
import com.lurepilot.backend.dto.InsightRecommendationPerformanceResponse;
import com.lurepilot.backend.dto.InsightTopLureResponse;
import com.lurepilot.backend.service.InsightsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/insights")
public class InsightsController {

    private final InsightsService insightsService;

    public InsightsController(InsightsService insightsService) {
        this.insightsService = insightsService;
    }

    @GetMapping("/top-lures")
    public List<InsightTopLureResponse> getTopLures(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Long spotId,
            @RequestParam(required = false) Long lureId,
            @RequestParam(required = false) Integer limit
    ) {
        return insightsService.getTopLures(dateFrom, dateTo, species, spotId, lureId, limit);
    }

    @GetMapping("/best-spots")
    public List<InsightBestSpotResponse> getBestSpots(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Long spotId,
            @RequestParam(required = false) Long lureId,
            @RequestParam(required = false) Integer limit
    ) {
        return insightsService.getBestSpots(dateFrom, dateTo, species, spotId, lureId, limit);
    }

    @GetMapping("/best-conditions")
    public List<InsightBestConditionResponse> getBestConditions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Long spotId,
            @RequestParam(required = false) Long lureId,
            @RequestParam(required = false) Integer limit
    ) {
        return insightsService.getBestConditions(dateFrom, dateTo, species, spotId, lureId, limit);
    }

    @GetMapping("/recommendation-performance")
    public List<InsightRecommendationPerformanceResponse> getRecommendationPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Long spotId,
            @RequestParam(required = false) Long lureId,
            @RequestParam(required = false) String recommendationType,
            @RequestParam(required = false) Integer limit
    ) {
        return insightsService.getRecommendationPerformance(dateFrom, dateTo, species, spotId, lureId, recommendationType, limit);
    }
}
