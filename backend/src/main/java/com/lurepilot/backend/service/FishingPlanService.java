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

import java.time.LocalDate;
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
        return searchPlans(null, null, null, null, null, null, null);
    }

    public List<FishingPlanResponse> searchPlans(String q, Long spotId, String targetSpecies, String waterClarity, String waterLevel, LocalDate dateFrom, LocalDate dateTo) {
        return fishingPlanRepository.findAll()
                .stream()
                .filter(plan -> matchesQuery(
                        q,
                        plan.getSpot().getName(),
                        plan.getTargetSpecies(),
                        plan.getWaterClarity(),
                        plan.getWaterLevel(),
                        plan.getNotes()
                ))
                .filter(plan -> spotId == null || spotId.equals(plan.getSpot().getId()))
                .filter(plan -> matchesContains(targetSpecies, plan.getTargetSpecies()))
                .filter(plan -> matchesExact(waterClarity, plan.getWaterClarity()))
                .filter(plan -> matchesExact(waterLevel, plan.getWaterLevel()))
                .filter(plan -> dateFrom == null || !plan.getPlannedDate().isBefore(dateFrom))
                .filter(plan -> dateTo == null || !plan.getPlannedDate().isAfter(dateTo))
                .map(this::toResponse)
                .toList();
    }

    public FishingPlanResponse getPlanById(Long id) {
        return fishingPlanRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));
    }

    public FishingPlanResponse updatePlan(Long id, CreateFishingPlanRequest request) {
        FishingPlan fishingPlan = fishingPlanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));
        FishingSpot spot = fishingSpotRepository.findById(request.spotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing spot not found"));

        fishingPlan.setSpot(spot);
        fishingPlan.setPlannedDate(request.plannedDate());
        fishingPlan.setPlannedTime(request.plannedTime());
        fishingPlan.setTargetSpecies(request.targetSpecies());
        fishingPlan.setWaterClarity(request.waterClarity());
        fishingPlan.setWaterLevel(request.waterLevel());
        fishingPlan.setNotes(request.notes());

        return toResponse(fishingPlanRepository.save(fishingPlan));
    }

    public void deletePlan(Long id) {
        if (!fishingPlanRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found");
        }

        fishingPlanRepository.deleteById(id);
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

    private boolean matchesQuery(String query, String... values) {
        if (query == null || query.isBlank()) {
            return true;
        }

        String normalizedQuery = normalize(query);
        for (String value : values) {
            if (value != null && normalize(value).contains(normalizedQuery)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesExact(String expected, String actual) {
        return expected == null || expected.isBlank() || normalize(expected).equals(normalize(actual));
    }

    private boolean matchesContains(String expected, String actual) {
        return expected == null || expected.isBlank() || normalize(actual).contains(normalize(expected));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
