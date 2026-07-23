package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.AddFishingPlanLureRequest;
import com.lurepilot.backend.dto.FishingPlanLureResponse;
import com.lurepilot.backend.service.FishingPlanLureService;
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
@RequestMapping("/api/plans/{planId}/lures")
public class FishingPlanLureController {

    private final FishingPlanLureService fishingPlanLureService;

    public FishingPlanLureController(FishingPlanLureService fishingPlanLureService) {
        this.fishingPlanLureService = fishingPlanLureService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FishingPlanLureResponse addLureToPlan(@PathVariable Long planId, @Valid @RequestBody AddFishingPlanLureRequest request) {
        return fishingPlanLureService.addLureToPlan(planId, request);
    }

    @GetMapping
    public List<FishingPlanLureResponse> getLuresByPlan(@PathVariable Long planId) {
        return fishingPlanLureService.getLuresByPlan(planId);
    }
}
