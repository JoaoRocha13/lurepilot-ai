package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateFishingPlanRequest;
import com.lurepilot.backend.dto.FishingPlanResponse;
import com.lurepilot.backend.dto.FishingPlanSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.service.FishingPlanService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
    public PagedResponse<FishingPlanSummaryResponse> getAllPlans(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long spotId,
            @RequestParam(required = false) String targetSpecies,
            @RequestParam(required = false) String waterClarity,
            @RequestParam(required = false) String waterLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return fishingPlanService.searchPlans(q, spotId, targetSpecies, waterClarity, waterLevel, dateFrom, dateTo, page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public FishingPlanResponse getPlanById(@PathVariable Long id) {
        return fishingPlanService.getPlanById(id);
    }

    @PutMapping("/{id}")
    public FishingPlanResponse updatePlan(@PathVariable Long id, @Valid @RequestBody CreateFishingPlanRequest request) {
        return fishingPlanService.updatePlan(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlan(@PathVariable Long id) {
        fishingPlanService.deletePlan(id);
    }
}
