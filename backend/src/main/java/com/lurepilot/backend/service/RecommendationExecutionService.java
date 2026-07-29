package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateRecommendationExecutionRequest;
import com.lurepilot.backend.dto.RecommendationExecutionResponse;
import com.lurepilot.backend.model.AiRecommendation;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.RecommendationExecution;
import com.lurepilot.backend.repository.AiRecommendationRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.RecommendationExecutionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class RecommendationExecutionService {

    private static final Set<String> PLAN_STEPS = Set.of("PLAN_A", "PLAN_B", "PLAN_C");
    private static final Set<String> SESSION_ADJUSTMENT_STEPS = Set.of(
            "SESSION_ADJUSTMENT",
            "IMMEDIATE_ACTION",
            "NEXT_TECHNIQUE",
            "FALLBACK_ACTION"
    );

    private final RecommendationExecutionRepository recommendationExecutionRepository;
    private final AiRecommendationRepository aiRecommendationRepository;
    private final FishingPlanRepository fishingPlanRepository;
    private final FishingSessionRepository fishingSessionRepository;

    public RecommendationExecutionService(
            RecommendationExecutionRepository recommendationExecutionRepository,
            AiRecommendationRepository aiRecommendationRepository,
            FishingPlanRepository fishingPlanRepository,
            FishingSessionRepository fishingSessionRepository
    ) {
        this.recommendationExecutionRepository = recommendationExecutionRepository;
        this.aiRecommendationRepository = aiRecommendationRepository;
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingSessionRepository = fishingSessionRepository;
    }

    @Transactional
    public RecommendationExecutionResponse createExecution(Long recommendationId, CreateRecommendationExecutionRequest request) {
        AiRecommendation recommendation = aiRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AI recommendation not found"));
        FishingSession session = resolveSession(recommendation, request.sessionId());
        FishingPlan plan = resolvePlan(recommendation, session);
        String recommendationStep = normalizeRecommendationStep(request.recommendationStep());
        validateStepForRecommendation(recommendation, recommendationStep);
        validateSessionLink(recommendation, session);

        RecommendationExecution execution = new RecommendationExecution(
                recommendation,
                plan,
                session,
                recommendation.getRecommendationType(),
                recommendation.getVersion(),
                recommendationStep,
                request.followed(),
                request.result(),
                request.success(),
                request.rating(),
                request.startedAt(),
                request.endedAt(),
                request.notes()
        );

        return toResponse(recommendationExecutionRepository.save(execution));
    }

    @Transactional(readOnly = true)
    public List<RecommendationExecutionResponse> getExecutionsByRecommendation(Long recommendationId) {
        if (!aiRecommendationRepository.existsById(recommendationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AI recommendation not found");
        }

        return recommendationExecutionRepository.findByRecommendationIdOrderByCreatedAtDescIdDesc(recommendationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecommendationExecutionResponse> getExecutionsByPlan(Long planId) {
        if (!fishingPlanRepository.existsById(planId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found");
        }

        return recommendationExecutionRepository.findByPlanIdOrderByCreatedAtDescIdDesc(planId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecommendationExecutionResponse> getExecutionsBySession(Long sessionId) {
        if (!fishingSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        return recommendationExecutionRepository.findBySessionIdOrderByCreatedAtDescIdDesc(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private FishingSession resolveSession(AiRecommendation recommendation, Long requestSessionId) {
        if (requestSessionId != null) {
            return fishingSessionRepository.findById(requestSessionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));
        }

        return recommendation.getSession();
    }

    private FishingPlan resolvePlan(AiRecommendation recommendation, FishingSession session) {
        if (recommendation.getPlan() != null) {
            return recommendation.getPlan();
        }

        return session == null ? null : session.getPlan();
    }

    private void validateSessionLink(AiRecommendation recommendation, FishingSession session) {
        if (session == null) {
            return;
        }

        if (recommendation.getSession() != null && !recommendation.getSession().getId().equals(session.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Session does not match the AI recommendation");
        }

        if (recommendation.getPlan() != null && session.getPlan() != null && !recommendation.getPlan().getId().equals(session.getPlan().getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Session plan does not match the AI recommendation plan");
        }
    }

    private void validateStepForRecommendation(AiRecommendation recommendation, String recommendationStep) {
        String recommendationType = recommendation.getRecommendationType();
        if ("PLAN".equalsIgnoreCase(recommendationType) && !PLAN_STEPS.contains(recommendationStep)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan recommendations accept PLAN_A, PLAN_B or PLAN_C");
        }

        if ("SESSION_ADJUSTMENT".equalsIgnoreCase(recommendationType) && !SESSION_ADJUSTMENT_STEPS.contains(recommendationStep)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session adjustments accept SESSION_ADJUSTMENT, IMMEDIATE_ACTION, NEXT_TECHNIQUE or FALLBACK_ACTION");
        }
    }

    private String normalizeRecommendationStep(String value) {
        String compact = value.trim().toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "");

        return switch (compact) {
            case "plana" -> "PLAN_A";
            case "planb" -> "PLAN_B";
            case "planc" -> "PLAN_C";
            case "sessionadjustment", "adjustment", "ajuste" -> "SESSION_ADJUSTMENT";
            case "immediateaction", "immediate", "acaoimediata" -> "IMMEDIATE_ACTION";
            case "nexttechnique", "technique", "tecnica" -> "NEXT_TECHNIQUE";
            case "fallbackaction", "fallback", "alternativa" -> "FALLBACK_ACTION";
            default -> value.trim().toUpperCase(Locale.ROOT).replace("-", "_").replace(" ", "_");
        };
    }

    private RecommendationExecutionResponse toResponse(RecommendationExecution execution) {
        return new RecommendationExecutionResponse(
                execution.getId(),
                execution.getRecommendation().getId(),
                execution.getPlan() == null ? null : execution.getPlan().getId(),
                execution.getSession() == null ? null : execution.getSession().getId(),
                execution.getRecommendationType(),
                execution.getRecommendationVersion(),
                execution.getRecommendationStep(),
                execution.getFollowed(),
                execution.getResult(),
                execution.getSuccess(),
                execution.getRating(),
                execution.getStartedAt(),
                execution.getEndedAt(),
                execution.getNotes(),
                execution.getCreatedAt()
        );
    }
}
