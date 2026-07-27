package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishingSessionRequest;
import com.lurepilot.backend.dto.FinishFishingSessionRequest;
import com.lurepilot.backend.dto.FishingSessionResponse;
import com.lurepilot.backend.dto.FishingSessionSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
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

    public PagedResponse<FishingSessionSummaryResponse> getAllSessions() {
        return searchSessions(null, null, null, null, null, null, null, null, null, null, 0, 20, "id", "asc");
    }

    public PagedResponse<FishingSessionSummaryResponse> searchSessions(
            String q,
            Long spotId,
            Long planId,
            String targetSpecies,
            String waterClarity,
            String waterLevel,
            String status,
            Boolean success,
            LocalDate dateFrom,
            LocalDate dateTo,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        List<FishingSession> filteredSessions = fishingSessionRepository.findAll()
                .stream()
                .filter(session -> matchesQuery(
                        q,
                        session.getSpot().getName(),
                        session.getTargetSpecies(),
                        session.getWaterClarity(),
                        session.getWaterLevel(),
                        session.getNotes(),
                        session.getResultSummary(),
                        session.getFinalNotes()
                ))
                .filter(session -> spotId == null || spotId.equals(session.getSpot().getId()))
                .filter(session -> planId == null || session.getPlan() != null && planId.equals(session.getPlan().getId()))
                .filter(session -> matchesContains(targetSpecies, session.getTargetSpecies()))
                .filter(session -> matchesExact(waterClarity, session.getWaterClarity()))
                .filter(session -> matchesExact(waterLevel, session.getWaterLevel()))
                .filter(session -> matchesExact(status, statusOrDefault(session).name()))
                .filter(session -> success == null || success.equals(session.getSuccess()))
                .filter(session -> dateFrom == null || !session.getDate().isBefore(dateFrom))
                .filter(session -> dateTo == null || !session.getDate().isAfter(dateTo))
                .toList();

        List<FishingSession> sortedSessions = filteredSessions.stream()
                .sorted(ListQuerySupport.applyDirection(fishingSessionComparator(sortBy), sortDirection))
                .toList();

        return ListQuerySupport.toPage(sortedSessions, page, size, this::toSummaryResponse);
    }

    public FishingSessionResponse getSessionById(Long id) {
        return fishingSessionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));
    }

    public FishingSessionResponse updateSession(Long id, CreateFishingSessionRequest request) {
        FishingSession session = findSession(id);
        FishingSpot spot = fishingSpotRepository.findById(request.spotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing spot not found"));
        FishingPlan plan = findPlanOrNull(request.planId());

        session.setSpot(spot);
        session.setPlan(plan);
        session.setDate(request.date());
        session.setStartTime(request.startTime());
        session.setEndTime(request.endTime());
        session.setTargetSpecies(request.targetSpecies());
        session.setWaterClarity(request.waterClarity());
        session.setWaterLevel(request.waterLevel());
        session.setNotes(request.notes());
        session.setSuccess(request.success());
        session.setDurationMinutes(calculateDurationMinutes(request.startTime(), request.endTime()));
        session.setStatus(resolveStatus(request.startTime(), request.endTime(), request.success()));

        return toResponse(fishingSessionRepository.save(session));
    }

    public void deleteSession(Long id) {
        if (!fishingSessionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        fishingSessionRepository.deleteById(id);
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

    private FishingSessionSummaryResponse toSummaryResponse(FishingSession fishingSession) {
        FishingSpot spot = fishingSession.getSpot();
        FishingPlan plan = fishingSession.getPlan();

        return new FishingSessionSummaryResponse(
                fishingSession.getId(),
                spot.getId(),
                spot.getName(),
                plan == null ? null : plan.getId(),
                fishingSession.getDate(),
                fishingSession.getStartTime(),
                fishingSession.getEndTime(),
                statusOrDefault(fishingSession).name().toLowerCase(Locale.ROOT),
                fishingSession.getTargetSpecies(),
                fishingSession.getSuccess(),
                fishingSession.getRating()
        );
    }

    private Comparator<FishingSession> fishingSessionComparator(String sortBy) {
        return switch (normalize(sortBy)) {
            case "date" -> ListQuerySupport.comparing(FishingSession::getDate);
            case "starttime" -> ListQuerySupport.comparing(FishingSession::getStartTime);
            case "endtime" -> ListQuerySupport.comparing(FishingSession::getEndTime);
            case "status" -> ListQuerySupport.comparing(session -> statusOrDefault(session).name());
            case "spotname" -> ListQuerySupport.comparing(session -> session.getSpot().getName());
            case "targetspecies" -> ListQuerySupport.comparing(FishingSession::getTargetSpecies);
            case "success" -> ListQuerySupport.comparing(FishingSession::getSuccess);
            case "rating" -> ListQuerySupport.comparing(FishingSession::getRating);
            case "createdat" -> ListQuerySupport.comparing(FishingSession::getCreatedAt);
            default -> ListQuerySupport.comparing(FishingSession::getId);
        };
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

    private FishingSessionStatus resolveStatus(LocalTime startTime, LocalTime endTime, Boolean success) {
        if (endTime != null || success != null) {
            return FishingSessionStatus.FINISHED;
        }

        if (startTime != null) {
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
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
