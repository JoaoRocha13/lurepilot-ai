package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.AddFishingPlanLureRequest;
import com.lurepilot.backend.dto.FishingPlanLureResponse;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingPlanLure;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.repository.FishingPlanLureRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.LureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FishingPlanLureService {

    private final FishingPlanLureRepository fishingPlanLureRepository;
    private final FishingPlanRepository fishingPlanRepository;
    private final LureRepository lureRepository;

    public FishingPlanLureService(FishingPlanLureRepository fishingPlanLureRepository, FishingPlanRepository fishingPlanRepository, LureRepository lureRepository) {
        this.fishingPlanLureRepository = fishingPlanLureRepository;
        this.fishingPlanRepository = fishingPlanRepository;
        this.lureRepository = lureRepository;
    }

    public FishingPlanLureResponse addLureToPlan(Long planId, AddFishingPlanLureRequest request) {
        FishingPlan plan = fishingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));
        Lure lure = lureRepository.findById(request.lureId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure not found"));

        if (fishingPlanLureRepository.existsByPlanIdAndLureId(planId, request.lureId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lure already added to fishing plan");
        }

        FishingPlanLure fishingPlanLure = new FishingPlanLure(plan, lure);
        return toResponse(fishingPlanLureRepository.save(fishingPlanLure));
    }

    public List<FishingPlanLureResponse> getLuresByPlan(Long planId) {
        if (!fishingPlanRepository.existsById(planId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found");
        }

        return fishingPlanLureRepository.findByPlanIdOrderByIdAsc(planId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private FishingPlanLureResponse toResponse(FishingPlanLure fishingPlanLure) {
        Lure lure = fishingPlanLure.getLure();
        LureLibraryItem libraryItem = lure.getLibraryItem();

        return new FishingPlanLureResponse(
                fishingPlanLure.getId(),
                fishingPlanLure.getPlan().getId(),
                lure.getId(),
                lure.getName(),
                lure.getType(),
                lure.getColor(),
                libraryItem == null ? null : libraryItem.getId(),
                libraryItem == null ? null : libraryItem.getName()
        );
    }
}
