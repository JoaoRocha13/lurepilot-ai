package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.PlannerContextResponse;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingPlanLure;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.repository.FishingPlanLureRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlannerContextService {

    private final FishingPlanRepository fishingPlanRepository;
    private final FishingPlanLureRepository fishingPlanLureRepository;
    private final FishingSessionRepository fishingSessionRepository;

    public PlannerContextService(
            FishingPlanRepository fishingPlanRepository,
            FishingPlanLureRepository fishingPlanLureRepository,
            FishingSessionRepository fishingSessionRepository
    ) {
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingPlanLureRepository = fishingPlanLureRepository;
        this.fishingSessionRepository = fishingSessionRepository;
    }

    public PlannerContextResponse buildContext(Long planId) {
        FishingPlan plan = fishingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));
        FishingSpot spot = plan.getSpot();

        List<PlannerContextResponse.PlannerContextLure> selectedLures = fishingPlanLureRepository.findByPlanIdOrderByIdAsc(planId)
                .stream()
                .map(this::toLureContext)
                .toList();

        List<PlannerContextResponse.PlannerContextSession> recentSpotSessions = fishingSessionRepository.findTop5BySpotIdOrderByDateDescIdDesc(spot.getId())
                .stream()
                .map(this::toSessionContext)
                .toList();

        List<PlannerContextResponse.PlannerContextSession> recentSpeciesSessions = fishingSessionRepository.findTop5ByTargetSpeciesIgnoreCaseOrderByDateDescIdDesc(plan.getTargetSpecies())
                .stream()
                .map(this::toSessionContext)
                .toList();

        return new PlannerContextResponse(
                toPlanContext(plan),
                toSpotContext(spot),
                selectedLures,
                recentSpotSessions,
                recentSpeciesSessions,
                buildDataQuality(selectedLures, recentSpotSessions, recentSpeciesSessions)
        );
    }

    private PlannerContextResponse.PlannerContextPlan toPlanContext(FishingPlan plan) {
        return new PlannerContextResponse.PlannerContextPlan(
                plan.getId(),
                plan.getPlannedDate(),
                plan.getPlannedTime(),
                plan.getTargetSpecies(),
                plan.getWaterClarity(),
                plan.getWaterLevel(),
                plan.getNotes()
        );
    }

    private PlannerContextResponse.PlannerContextSpot toSpotContext(FishingSpot spot) {
        return new PlannerContextResponse.PlannerContextSpot(
                spot.getId(),
                spot.getName(),
                spot.getDescription(),
                spot.getLatitude(),
                spot.getLongitude(),
                spot.getWaterType(),
                spot.getFavoriteSpecies()
        );
    }

    private PlannerContextResponse.PlannerContextLure toLureContext(FishingPlanLure fishingPlanLure) {
        Lure lure = fishingPlanLure.getLure();
        LureLibraryItem libraryItem = lure.getLibraryItem();

        return new PlannerContextResponse.PlannerContextLure(
                lure.getId(),
                lure.getName(),
                lure.getType(),
                lure.getColor(),
                lure.getSize(),
                lure.getWeight(),
                lure.getBrand(),
                lure.getTargetSpecies(),
                lure.getWaterType(),
                libraryItem == null ? null : libraryItem.getId(),
                libraryItem == null ? null : libraryItem.getName()
        );
    }

    private PlannerContextResponse.PlannerContextSession toSessionContext(FishingSession session) {
        FishingSpot spot = session.getSpot();

        return new PlannerContextResponse.PlannerContextSession(
                session.getId(),
                spot.getId(),
                spot.getName(),
                session.getDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getTargetSpecies(),
                session.getWaterClarity(),
                session.getWaterLevel(),
                session.getSuccess(),
                session.getNotes()
        );
    }

    private PlannerContextResponse.PlannerContextDataQuality buildDataQuality(
            List<PlannerContextResponse.PlannerContextLure> selectedLures,
            List<PlannerContextResponse.PlannerContextSession> recentSpotSessions,
            List<PlannerContextResponse.PlannerContextSession> recentSpeciesSessions
    ) {
        List<String> warnings = new ArrayList<>();

        if (selectedLures.isEmpty()) {
            warnings.add("No lures selected for this fishing plan yet.");
        }

        if (recentSpotSessions.isEmpty()) {
            warnings.add("No recent history found for this spot.");
        }

        if (recentSpeciesSessions.isEmpty()) {
            warnings.add("No recent history found for this target species.");
        }

        String confidenceHint = warnings.isEmpty() ? "medium" : "low";

        return new PlannerContextResponse.PlannerContextDataQuality(
                selectedLures.size(),
                recentSpotSessions.size(),
                recentSpeciesSessions.size(),
                confidenceHint,
                warnings
        );
    }
}
