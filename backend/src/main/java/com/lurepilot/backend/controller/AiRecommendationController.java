package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.AiPlanRecommendationResponse;
import com.lurepilot.backend.dto.CreateAiPlanRecommendationRequest;
import com.lurepilot.backend.service.AiRecommendationService;
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
@RequestMapping("/api/recommendations")
public class AiRecommendationController {

    private final AiRecommendationService aiRecommendationService;

    public AiRecommendationController(AiRecommendationService aiRecommendationService) {
        this.aiRecommendationService = aiRecommendationService;
    }

    @PostMapping("/plan")
    @ResponseStatus(HttpStatus.CREATED)
    public AiPlanRecommendationResponse createPlanRecommendation(@Valid @RequestBody CreateAiPlanRecommendationRequest request) {
        return aiRecommendationService.createPlanRecommendation(request);
    }

    @GetMapping("/plans/{planId}")
    public List<AiPlanRecommendationResponse> getRecommendationsByPlan(@PathVariable Long planId) {
        return aiRecommendationService.getRecommendationsByPlan(planId);
    }
}
