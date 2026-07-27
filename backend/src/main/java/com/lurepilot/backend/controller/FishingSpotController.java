package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateFishingSpotRequest;
import com.lurepilot.backend.dto.FishingSpotResponse;
import com.lurepilot.backend.dto.FishingSpotSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.service.FishingSpotService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api/spots")
public class FishingSpotController {

    private final FishingSpotService fishingSpotService;

    public FishingSpotController(FishingSpotService fishingSpotService) {
        this.fishingSpotService = fishingSpotService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FishingSpotResponse createSpot(@Valid @RequestBody CreateFishingSpotRequest request) {
        return fishingSpotService.createSpot(request);
    }

    @GetMapping
    public PagedResponse<FishingSpotSummaryResponse> getAllSpots(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String waterType,
            @RequestParam(required = false) String favoriteSpecies,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return fishingSpotService.searchSpots(q, waterType, favoriteSpecies, page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public FishingSpotResponse getSpotById(@PathVariable Long id) {
        return fishingSpotService.getSpotById(id);
    }

    @PutMapping("/{id}")
    public FishingSpotResponse updateSpot(@PathVariable Long id, @Valid @RequestBody CreateFishingSpotRequest request) {
        return fishingSpotService.updateSpot(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpot(@PathVariable Long id) {
        fishingSpotService.deleteSpot(id);
    }
}
