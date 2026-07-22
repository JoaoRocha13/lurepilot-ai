package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishingSessionRequest;
import com.lurepilot.backend.dto.FishingSessionResponse;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.FishingSpotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FishingSessionService {

    private final FishingSessionRepository fishingSessionRepository;
    private final FishingSpotRepository fishingSpotRepository;
    private final FishingPlanRepository fishingPlanRepository;

    public FishingSessionService(FishingSessionRepository fishingSessionRepository, FishingSpotRepository fishingSpotRepository, FishingPlanRepository fishingPlanRepository) {
        this.fishingSessionRepository = fishingSessionRepository;
        this.fishingSpotRepository = fishingSpotRepository;
        this.fishingPlanRepository = fishingPlanRepository;
    }

    public FishingSessionResponse createSession(CreateFishingSessionRequest request) {
        FishingSpot spot = fishingSpotRepository.findById(request.spotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing spot not found"));
        FishingPlan plan = findPlanOrNull(request.planId());

        FishingSession fishingSession = new FishingSession(
                spot,
                plan,
                request.date(),
                request.startTime(),
                request.endTime(),
                request.targetSpecies(),
                request.waterClarity(),
                request.waterLevel(),
                request.notes(),
                request.success()
        );

        return toResponse(fishingSessionRepository.save(fishingSession));
    }

    public List<FishingSessionResponse> getAllSessions() {
        return fishingSessionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FishingSessionResponse getSessionById(Long id) {
        return fishingSessionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));
    }

    private FishingPlan findPlanOrNull(Long planId) {
        if (planId == null) {
            return null;
        }

        return fishingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));
    }

    private FishingSessionResponse toResponse(FishingSession fishingSession) {
        FishingSpot spot = fishingSession.getSpot();
        FishingPlan plan = fishingSession.getPlan();

        return new FishingSessionResponse(
                fishingSession.getId(),
                spot.getId(),
                spot.getName(),
                plan == null ? null : plan.getId(),
                fishingSession.getDate(),
                fishingSession.getStartTime(),
                fishingSession.getEndTime(),
                fishingSession.getTargetSpecies(),
                fishingSession.getWaterClarity(),
                fishingSession.getWaterLevel(),
                fishingSession.getNotes(),
                fishingSession.getSuccess(),
                fishingSession.getCreatedAt()
        );
    }
}
