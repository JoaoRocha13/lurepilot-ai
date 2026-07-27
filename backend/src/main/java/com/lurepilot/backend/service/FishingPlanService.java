package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishingPlanRequest;
import com.lurepilot.backend.dto.FishingPlanResponse;
import com.lurepilot.backend.dto.FishingPlanSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSpotRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Map;

@Service
public class FishingPlanService {

    private static final Map<String, String> SORT_FIELDS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("planneddate", "plannedDate"),
            Map.entry("date", "plannedDate"),
            Map.entry("plannedtime", "plannedTime"),
            Map.entry("time", "plannedTime"),
            Map.entry("spotname", "spot.name"),
            Map.entry("targetspecies", "targetSpecies"),
            Map.entry("waterclarity", "waterClarity"),
            Map.entry("waterlevel", "waterLevel"),
            Map.entry("createdat", "createdAt")
    );

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

    public PagedResponse<FishingPlanSummaryResponse> getAllPlans() {
        return searchPlans(null, null, null, null, null, null, null, 0, 20, "id", "asc");
    }

    public PagedResponse<FishingPlanSummaryResponse> searchPlans(
            String q,
            Long spotId,
            String targetSpecies,
            String waterClarity,
            String waterLevel,
            LocalDate dateFrom,
            LocalDate dateTo,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Specification<FishingPlan> specification = Specification.allOf(
                SearchSpecifications.containsAny(q, "spot.name", "targetSpecies", "waterClarity", "waterLevel", "notes"),
                SearchSpecifications.equalsValue(spotId, "spot.id"),
                SearchSpecifications.contains(targetSpecies, "targetSpecies"),
                SearchSpecifications.equalsIgnoreCase(waterClarity, "waterClarity"),
                SearchSpecifications.equalsIgnoreCase(waterLevel, "waterLevel"),
                SearchSpecifications.dateFrom(dateFrom, "plannedDate"),
                SearchSpecifications.dateTo(dateTo, "plannedDate")
        );
        Pageable pageable = ListQuerySupport.toPageable(page, size, sortBy, sortDirection, SORT_FIELDS);
        Page<FishingPlan> plans = fishingPlanRepository.findAll(specification, pageable);

        return ListQuerySupport.toPagedResponse(plans, this::toSummaryResponse);
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

    private FishingPlanSummaryResponse toSummaryResponse(FishingPlan fishingPlan) {
        FishingSpot spot = fishingPlan.getSpot();

        return new FishingPlanSummaryResponse(
                fishingPlan.getId(),
                spot.getId(),
                spot.getName(),
                fishingPlan.getPlannedDate(),
                fishingPlan.getPlannedTime(),
                fishingPlan.getTargetSpecies(),
                fishingPlan.getWaterClarity(),
                fishingPlan.getWaterLevel()
        );
    }

}
