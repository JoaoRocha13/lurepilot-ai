package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateFishSpeciesRequest;
import com.lurepilot.backend.dto.FishSpeciesResponse;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<FishSpeciesResponse> getAllFishSpecies() {
        return fishSpeciesService.getAllFishSpecies();
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
