package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishingPlanRequest;
import com.lurepilot.backend.dto.FishingPlanResponse;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSpotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FishingPlanService {

    private final FishingPlanRepository fishingPlanRepository;
    private final FishingSpotRepository fishingSpotRepository;

    public FishingPlanService(FishingPlanRepository fishingPlanRepository, FishingSpotRepository fishingSpotRepository) {
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingSpotRepository = fishingSpotRepository;
    }

    public FishingPlanResponse createPlan(CreateFishingPlanRequest request) {
        FishingSpot spot = fishingSpotRepository.findById(request.spotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing spot not found"));

        FishingPlan fishingPlan = new FishingPlan(
                spot,
                request.plannedDate(),
                request.plannedTime(),
                request.targetSpecies(),
                request.waterClarity(),
                request.waterLevel(),
                request.notes()
        );

        return toResponse(fishingPlanRepository.save(fishingPlan));
    }

    public List<FishingPlanResponse> getAllPlans() {
        return fishingPlanRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FishingPlanResponse getPlanById(Long id) {
        return fishingPlanRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));
    }

    private FishingPlanResponse toResponse(FishingPlan fishingPlan) {
        FishingSpot spot = fishingPlan.getSpot();

        return new FishingPlanResponse(
                fishingPlan.getId(),
                spot.getId(),
                spot.getName(),
                fishingPlan.getPlannedDate(),
                fishingPlan.getPlannedTime(),
                fishingPlan.getTargetSpecies(),
                fishingPlan.getWaterClarity(),
                fishingPlan.getWaterLevel(),
                fishingPlan.getNotes(),
                fishingPlan.getCreatedAt()
        );
    }
}
