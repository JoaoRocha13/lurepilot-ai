package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateFishingPlanRequest;
import com.lurepilot.backend.dto.FishingPlanResponse;
import com.lurepilot.backend.service.FishingPlanService;
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
@RequestMapping("/api/plans")
public class FishingPlanController {

    private final FishingPlanService fishingPlanService;

    public FishingPlanController(FishingPlanService fishingPlanService) {
        this.fishingPlanService = fishingPlanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FishingPlanResponse createPlan(@Valid @RequestBody CreateFishingPlanRequest request) {
        return fishingPlanService.createPlan(request);
    }

    @GetMapping
    public List<FishingPlanResponse> getAllPlans() {
        return fishingPlanService.getAllPlans();
    }

    @GetMapping("/{id}")
    public FishingPlanResponse getPlanById(@PathVariable Long id) {
        return fishingPlanService.getPlanById(id);
    }
}
