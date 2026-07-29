package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateRecommendationExecutionRequest;
import com.lurepilot.backend.dto.RecommendationExecutionResponse;
import com.lurepilot.backend.service.RecommendationExecutionService;
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
@RequestMapping("/api")
public class RecommendationExecutionController {

    private final RecommendationExecutionService recommendationExecutionService;

    public RecommendationExecutionController(RecommendationExecutionService recommendationExecutionService) {
        this.recommendationExecutionService = recommendationExecutionService;
    }

    @PostMapping("/recommendations/{recommendationId}/executions")
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationExecutionResponse createExecution(
            @PathVariable Long recommendationId,
            @Valid @RequestBody CreateRecommendationExecutionRequest request
    ) {
        return recommendationExecutionService.createExecution(recommendationId, request);
    }

    @GetMapping("/recommendations/{recommendationId}/executions")
    public List<RecommendationExecutionResponse> getExecutionsByRecommendation(@PathVariable Long recommendationId) {
        return recommendationExecutionService.getExecutionsByRecommendation(recommendationId);
    }

    @GetMapping("/plans/{planId}/recommendation-executions")
    public List<RecommendationExecutionResponse> getExecutionsByPlan(@PathVariable Long planId) {
        return recommendationExecutionService.getExecutionsByPlan(planId);
    }

    @GetMapping("/sessions/{sessionId}/recommendation-executions")
    public List<RecommendationExecutionResponse> getExecutionsBySession(@PathVariable Long sessionId) {
        return recommendationExecutionService.getExecutionsBySession(sessionId);
    }
}
