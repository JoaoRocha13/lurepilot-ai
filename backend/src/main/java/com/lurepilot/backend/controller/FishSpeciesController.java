package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateFishSpeciesRequest;
import com.lurepilot.backend.dto.FishSpeciesResponse;
import com.lurepilot.backend.dto.FishSpeciesSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.service.FishSpeciesService;
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
@RequestMapping("/api/fish")
public class FishSpeciesController {

    private final FishSpeciesService fishSpeciesService;

    public FishSpeciesController(FishSpeciesService fishSpeciesService) {
        this.fishSpeciesService = fishSpeciesService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FishSpeciesResponse createFishSpecies(@Valid @RequestBody CreateFishSpeciesRequest request) {
        return fishSpeciesService.createFishSpecies(request);
    }

    @GetMapping
    public PagedResponse<FishSpeciesSummaryResponse> getAllFishSpecies(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String strikeZone,
            @RequestParam(required = false) String waterEnvironment,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return fishSpeciesService.searchFishSpecies(q, strikeZone, waterEnvironment, page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public FishSpeciesResponse getFishSpeciesById(@PathVariable Long id) {
        return fishSpeciesService.getFishSpeciesById(id);
    }

    @PutMapping("/{id}")
    public FishSpeciesResponse updateFishSpecies(@PathVariable Long id, @Valid @RequestBody CreateFishSpeciesRequest request) {
        return fishSpeciesService.updateFishSpecies(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFishSpecies(@PathVariable Long id) {
        fishSpeciesService.deleteFishSpecies(id);
    }
}
