package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishingSessionRequest;
import com.lurepilot.backend.dto.FinishFishingSessionRequest;
import com.lurepilot.backend.dto.FishingSessionResponse;
import com.lurepilot.backend.dto.StartFishingSessionRequest;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.FishingSpotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

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
        fishingSession.setDurationMinutes(calculateDurationMinutes(request.startTime(), request.endTime()));

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

    public FishingSessionResponse startSession(Long id, StartFishingSessionRequest request) {
        FishingSession session = findSession(id);

        if (statusOrDefault(session) == FishingSessionStatus.FINISHED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Fishing session is already finished");
        }

        LocalTime startTime = request != null && request.startTime() != null ? request.startTime() : LocalTime.now();
        session.setStartTime(startTime);
        session.setStatus(FishingSessionStatus.ACTIVE);

        if (request != null && request.notes() != null && !request.notes().isBlank()) {
            session.setNotes(request.notes());
        }

        return toResponse(fishingSessionRepository.save(session));
    }

    public FishingSessionResponse finishSession(Long id, FinishFishingSessionRequest request) {
        FishingSession session = findSession(id);
        FinishFishingSessionRequest finishRequest = request == null
                ? new FinishFishingSessionRequest(null, null, null, null, null)
                : request;

        LocalTime endTime = finishRequest.endTime() != null ? finishRequest.endTime() : LocalTime.now();
        session.setEndTime(endTime);
        session.setStatus(FishingSessionStatus.FINISHED);
        if (finishRequest.success() != null) {
            session.setSuccess(finishRequest.success());
        }
        if (finishRequest.resultSummary() != null) {
            session.setResultSummary(finishRequest.resultSummary());
        }
        if (finishRequest.finalNotes() != null) {
            session.setFinalNotes(finishRequest.finalNotes());
        }
        if (finishRequest.rating() != null) {
            session.setRating(finishRequest.rating());
        }
        session.setDurationMinutes(calculateDurationMinutes(session.getStartTime(), endTime));

        return toResponse(fishingSessionRepository.save(session));
    }

    private FishingPlan findPlanOrNull(Long planId) {
        if (planId == null) {
            return null;
        }

        return fishingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));
    }

    private FishingSession findSession(Long id) {
        return fishingSessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));
    }

    private FishingSessionResponse toResponse(FishingSession fishingSession) {
        FishingSpot spot = fishingSession.getSpot();
        FishingPlan plan = fishingSession.getPlan();
        FishingSessionStatus status = statusOrDefault(fishingSession);

        return new FishingSessionResponse(
                fishingSession.getId(),
                spot.getId(),
                spot.getName(),
                plan == null ? null : plan.getId(),
                fishingSession.getDate(),
                fishingSession.getStartTime(),
                fishingSession.getEndTime(),
                status.name().toLowerCase(Locale.ROOT),
                fishingSession.getTargetSpecies(),
                fishingSession.getWaterClarity(),
                fishingSession.getWaterLevel(),
                fishingSession.getNotes(),
                fishingSession.getSuccess(),
                fishingSession.getDurationMinutes(),
                fishingSession.getResultSummary(),
                fishingSession.getFinalNotes(),
                fishingSession.getRating(),
                fishingSession.getCreatedAt()
        );
    }

    private FishingSessionStatus statusOrDefault(FishingSession session) {
        if (session.getStatus() != null) {
            return session.getStatus();
        }

        if (session.getEndTime() != null || session.getSuccess() != null) {
            return FishingSessionStatus.FINISHED;
        }

        if (session.getStartTime() != null) {
            return FishingSessionStatus.ACTIVE;
        }

        return FishingSessionStatus.PLANNED;
    }

    private Long calculateDurationMinutes(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            return null;
        }

        long minutes = Duration.between(startTime, endTime).toMinutes();
        if (minutes < 0) {
            minutes += Duration.ofHours(24).toMinutes();
        }

        return minutes;
    }
}
