package com.lurepilot.backend.service;

import com.lurepilot.backend.client.LmStudioClient;
import com.lurepilot.backend.dto.AiRecommendationDebugResponse;
import com.lurepilot.backend.dto.AiLureRankingResponse;
import com.lurepilot.backend.dto.AiPlanRecommendationResponse;
import com.lurepilot.backend.dto.AiPlanResult;
import com.lurepilot.backend.dto.AiSessionAdjustmentResponse;
import com.lurepilot.backend.dto.AiSessionAdjustmentResult;
import com.lurepilot.backend.dto.CreateAiPlanRecommendationRequest;
import com.lurepilot.backend.dto.CreateSessionAdjustmentRequest;
import com.lurepilot.backend.dto.PlannerContextResponse;
import com.lurepilot.backend.model.AiRecommendation;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingPlanLure;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.model.SessionEvent;
import com.lurepilot.backend.model.SessionLure;
import com.lurepilot.backend.model.WeatherSnapshot;
import com.lurepilot.backend.repository.AiRecommendationRepository;
import com.lurepilot.backend.repository.FishingPlanLureRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.SessionEventRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AiRecommendationService {

    private static final String PLAN_RECOMMENDATION = "PLAN";
    private static final String SESSION_ADJUSTMENT = "SESSION_ADJUSTMENT";

    private static final TypeReference<List<AiLureRankingResponse>> LURE_RANKING_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    private final AiRecommendationRepository aiRecommendationRepository;
    private final FishingPlanRepository fishingPlanRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final FishingPlanLureRepository fishingPlanLureRepository;
    private final SessionLureRepository sessionLureRepository;
    private final SessionEventRepository sessionEventRepository;
    private final WeatherSnapshotRepository weatherSnapshotRepository;
    private final WeatherSnapshotService weatherSnapshotService;
    private final PlannerContextService plannerContextService;
    private final LmStudioClient lmStudioClient;
    private final ObjectMapper objectMapper;

    public AiRecommendationService(
            AiRecommendationRepository aiRecommendationRepository,
            FishingPlanRepository fishingPlanRepository,
            FishingSessionRepository fishingSessionRepository,
            FishingPlanLureRepository fishingPlanLureRepository,
            SessionLureRepository sessionLureRepository,
            SessionEventRepository sessionEventRepository,
            WeatherSnapshotRepository weatherSnapshotRepository,
            WeatherSnapshotService weatherSnapshotService,
            PlannerContextService plannerContextService,
            LmStudioClient lmStudioClient,
            ObjectMapper objectMapper
    ) {
        this.aiRecommendationRepository = aiRecommendationRepository;
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.fishingPlanLureRepository = fishingPlanLureRepository;
        this.sessionLureRepository = sessionLureRepository;
        this.sessionEventRepository = sessionEventRepository;
        this.weatherSnapshotRepository = weatherSnapshotRepository;
        this.weatherSnapshotService = weatherSnapshotService;
        this.plannerContextService = plannerContextService;
        this.lmStudioClient = lmStudioClient;
        this.objectMapper = objectMapper;
    }

    public AiPlanRecommendationResponse createPlanRecommendation(CreateAiPlanRecommendationRequest request) {
        FishingPlan plan = fishingPlanRepository.findById(request.planId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));

        ensureWeatherSnapshotForPlan(plan);
        PlannerContextResponse context = plannerContextService.buildContext(request.planId());
        String contextJson = writeJson(context);
        String rawResponse = callLmStudio(contextJson);
        AiPlanResult result = validatePlanResult(parsePlanResult(rawResponse), context);

        AiRecommendation recommendation = new AiRecommendation(
                plan,
                null,
                PLAN_RECOMMENDATION,
                nextPlanVersion(plan.getId()),
                contextJson,
                rawResponse,
                result.summary(),
                writeJson(nullToEmpty(result.lureRanking())),
                result.planA(),
                result.planB(),
                result.planC(),
                writeJson(nullToEmpty(result.avoid())),
                result.confidence(),
                writeJson(nullToEmpty(result.warnings()))
        );

        return toResponse(aiRecommendationRepository.save(recommendation));
    }

    public AiSessionAdjustmentResponse createSessionAdjustment(CreateSessionAdjustmentRequest request) {
        FishingSession session = fishingSessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));

        SessionAdjustmentContext context = buildSessionAdjustmentContext(session, request);
        String contextJson = writeJson(context);
        String rawResponse = callLmStudioForSessionAdjustment(contextJson);
        AiSessionAdjustmentResult result = validateSessionAdjustmentResult(parseSessionAdjustmentResult(rawResponse), context);

        AiRecommendation recommendation = new AiRecommendation(
                session.getPlan(),
                session,
                SESSION_ADJUSTMENT,
                nextSessionAdjustmentVersion(session.getId()),
                contextJson,
                rawResponse,
                result.summary(),
                writeJson(nullToEmpty(result.lureRanking())),
                result.immediateAction(),
                result.nextTechnique(),
                result.fallbackAction(),
                writeJson(nullToEmpty(result.avoid())),
                result.confidence(),
                writeJson(nullToEmpty(result.warnings()))
        );

        return toSessionAdjustmentResponse(aiRecommendationRepository.save(recommendation));
    }

    public List<AiPlanRecommendationResponse> getRecommendationsByPlan(Long planId) {
        if (!fishingPlanRepository.existsById(planId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found");
        }

        return aiRecommendationRepository.findByPlanIdOrderByCreatedAtDescIdDesc(planId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AiRecommendationDebugResponse getRecommendationDebug(Long id) {
        AiRecommendation recommendation = aiRecommendationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AI recommendation not found"));

        return new AiRecommendationDebugResponse(
                recommendation.getId(),
                recommendation.getPlan().getId(),
                recommendation.getSession() == null ? null : recommendation.getSession().getId(),
                recommendation.getRecommendationType(),
                recommendation.getVersion(),
                recommendation.getContextJson(),
                recommendation.getRawResponse(),
                recommendation.getCreatedAt()
        );
    }

    private String callLmStudio(String contextJson) {
        String systemMessage = """
                Es o LurePilot AI, um copiloto de pesca pratico.
                Responde sempre em portugues de Portugal.
                Usa apenas o contexto fornecido e conhecimento geral seguro.
                Nao inventes dados factuais, meteorologicos, legais ou historicos.
                No lureRanking, usa exclusivamente lures presentes em selectedLures.
                Nao menciones nem recomendes lures que nao estejam em selectedLures.
                Nos campos planA, planB e planC, menciona apenas lures presentes em selectedLures.
                Devolve apenas JSON valido, sem markdown.
                """;

        String userMessage = """
                Cria uma recomendacao de pesca em formato JSON com estes campos:
                summary: string curta.
                lureRanking: lista de objetos com rank, lure e reason.
                Em lureRanking, o campo lure tem de ser exatamente um dos nomes em selectedLures.
                planA: string com plano 0-20 min.
                planB: string com ajuste 20-40 min.
                planC: string com alternativa 40-60 min.
                planA, planB e planC nao podem recomendar lures fora de selectedLures.
                avoid: lista de strings com coisas a evitar.
                confidence: low, medium ou high.
                warnings: lista de strings quando o contexto for limitado.

                Contexto estruturado:
                %s
                """.formatted(contextJson);

        try {
            return lmStudioClient.createChatCompletion(systemMessage, userMessage);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not get recommendation from LM Studio", ex);
        }
    }

    private String callLmStudioForSessionAdjustment(String contextJson) {
        String systemMessage = """
                Es o LurePilot AI, um copiloto de pesca durante a sessao.
                Responde sempre em portugues de Portugal.
                Usa apenas o contexto fornecido e conhecimento geral seguro.
                Nao inventes dados factuais, meteorologicos, legais ou historicos.
                Usa exclusivamente lures presentes em allowedLures.
                Devolve apenas JSON valido, sem markdown.
                """;

        String userMessage = """
                Ajusta a estrategia da sessao em formato JSON com estes campos:
                summary: string curta.
                lureRanking: lista de objetos com rank, lure e reason; lure tem de ser exatamente um dos nomes em allowedLures.
                immediateAction: o que fazer nos proximos 10-15 min.
                nextTechnique: tecnica ou cadencia a testar a seguir.
                fallbackAction: alternativa se continuar sem resultado.
                avoid: lista de strings com coisas a evitar agora.
                confidence: low, medium ou high.
                warnings: lista de strings quando o contexto for limitado.

                Contexto estruturado:
                %s
                """.formatted(contextJson);

        try {
            return lmStudioClient.createChatCompletion(systemMessage, userMessage);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not get session adjustment from LM Studio", ex);
        }
    }

    private AiPlanResult parsePlanResult(String rawResponse) {
        String json = extractJsonObject(rawResponse);

        try {
            return objectMapper.readValue(json, AiPlanResult.class);
        } catch (Exception ex) {
            return new AiPlanResult(
                    rawResponse,
                    List.of(),
                    null,
                    null,
                    null,
                    List.of("A resposta da IA nao veio no formato JSON esperado."),
                    "low",
                    List.of("A recomendacao foi guardada como resposta raw porque o JSON nao foi interpretado.")
            );
        }
    }

    private AiSessionAdjustmentResult parseSessionAdjustmentResult(String rawResponse) {
        String json = extractJsonObject(rawResponse);

        try {
            return objectMapper.readValue(json, AiSessionAdjustmentResult.class);
        } catch (Exception ex) {
            return new AiSessionAdjustmentResult(
                    rawResponse,
                    List.of(),
                    null,
                    null,
                    null,
                    List.of("A resposta da IA nao veio no formato JSON esperado."),
                    "low",
                    List.of("O ajuste foi guardado como resposta raw porque o JSON nao foi interpretado.")
            );
        }
    }

    private String extractJsonObject(String rawResponse) {
        int start = rawResponse.indexOf('{');
        int end = rawResponse.lastIndexOf('}');

        if (start >= 0 && end > start) {
            return rawResponse.substring(start, end + 1);
        }

        return rawResponse;
    }

    private AiPlanResult validatePlanResult(AiPlanResult result, PlannerContextResponse context) {
        Set<String> allowedLureNames = context.selectedLures()
                .stream()
                .map(PlannerContextResponse.PlannerContextLure::name)
                .map(this::normalize)
                .collect(Collectors.toSet());

        List<AiLureRankingResponse> originalRanking = nullToEmpty(result.lureRanking());
        List<String> removedLures = originalRanking.stream()
                .filter(lureRanking -> !allowedLureNames.contains(normalize(lureRanking.lure())))
                .map(AiLureRankingResponse::lure)
                .toList();

        List<AiLureRankingResponse> validatedRanking = new ArrayList<>();
        int rank = 1;
        for (AiLureRankingResponse lureRanking : originalRanking) {
            if (allowedLureNames.contains(normalize(lureRanking.lure()))) {
                validatedRanking.add(new AiLureRankingResponse(rank, lureRanking.lure(), lureRanking.reason()));
                rank++;
            }
        }

        List<String> warnings = new ArrayList<>(nullToEmpty(result.warnings()));
        if (!removedLures.isEmpty()) {
            warnings.add("A IA sugeriu lures fora de selectedLures e foram removidas: " + String.join(", ", removedLures));
        }

        String confidence = result.confidence();
        if (validatedRanking.isEmpty()) {
            warnings.add("A IA nao devolveu nenhuma lure valida do plano.");
            confidence = "low";
        }

        String planA = withFallback(result.planA(), "Comecar pela lure melhor classificada e pescar de forma controlada durante 20 minutos.");
        String planB = withFallback(result.planB(), "Se nao houver sinais, alterar cadencia ou zona mantendo as lures selecionadas.");
        String planC = withFallback(result.planC(), "Se continuar sem resultado, reduzir ritmo e focar estruturas ou zonas de sombra.");

        if (!removedLures.isEmpty()) {
            confidence = "low";
        }

        return new AiPlanResult(
                result.summary(),
                validatedRanking,
                planA,
                planB,
                planC,
                nullToEmpty(result.avoid()),
                confidence,
                warnings
        );
    }

    private AiSessionAdjustmentResult validateSessionAdjustmentResult(AiSessionAdjustmentResult result, SessionAdjustmentContext context) {
        Set<String> allowedLureNames = context.allowedLures()
                .stream()
                .map(SessionAdjustmentLure::name)
                .map(this::normalize)
                .collect(Collectors.toSet());

        List<AiLureRankingResponse> originalRanking = nullToEmpty(result.lureRanking());
        List<String> removedLures = originalRanking.stream()
                .filter(lureRanking -> !allowedLureNames.contains(normalize(lureRanking.lure())))
                .map(AiLureRankingResponse::lure)
                .toList();

        List<AiLureRankingResponse> validatedRanking = new ArrayList<>();
        int rank = 1;
        for (AiLureRankingResponse lureRanking : originalRanking) {
            if (allowedLureNames.contains(normalize(lureRanking.lure()))) {
                validatedRanking.add(new AiLureRankingResponse(rank, lureRanking.lure(), lureRanking.reason()));
                rank++;
            }
        }

        List<String> warnings = new ArrayList<>(nullToEmpty(result.warnings()));
        String confidence = result.confidence();

        if (!removedLures.isEmpty()) {
            warnings.add("A IA sugeriu lures fora de allowedLures e foram removidas: " + String.join(", ", removedLures));
            confidence = "low";
        }

        if (validatedRanking.isEmpty()) {
            warnings.add("A IA nao devolveu nenhuma lure valida para ajustar a sessao.");
            confidence = "low";
        }

        return new AiSessionAdjustmentResult(
                result.summary(),
                validatedRanking,
                withFallback(result.immediateAction(), "Manter abordagem simples durante 10-15 minutos e observar sinais de atividade."),
                withFallback(result.nextTechnique(), "Alterar cadencia antes de trocar completamente de zona."),
                withFallback(result.fallbackAction(), "Se continuar sem resposta, mudar para uma zona com estrutura ou sombra."),
                nullToEmpty(result.avoid()),
                confidence,
                warnings
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private AiPlanRecommendationResponse toResponse(AiRecommendation recommendation) {
        return new AiPlanRecommendationResponse(
                recommendation.getId(),
                recommendation.getPlan().getId(),
                recommendation.getVersion(),
                recommendation.getSummary(),
                readJson(recommendation.getLureRankingJson(), LURE_RANKING_TYPE),
                recommendation.getPlanA(),
                recommendation.getPlanB(),
                recommendation.getPlanC(),
                readJson(recommendation.getAvoidJson(), STRING_LIST_TYPE),
                recommendation.getConfidence(),
                readJson(recommendation.getWarningsJson(), STRING_LIST_TYPE),
                recommendation.getCreatedAt()
        );
    }

    private AiSessionAdjustmentResponse toSessionAdjustmentResponse(AiRecommendation recommendation) {
        return new AiSessionAdjustmentResponse(
                recommendation.getId(),
                recommendation.getSession().getId(),
                recommendation.getPlan() == null ? null : recommendation.getPlan().getId(),
                recommendation.getVersion(),
                recommendation.getSummary(),
                readJson(recommendation.getLureRankingJson(), LURE_RANKING_TYPE),
                recommendation.getPlanA(),
                recommendation.getPlanB(),
                recommendation.getPlanC(),
                readJson(recommendation.getAvoidJson(), STRING_LIST_TYPE),
                recommendation.getConfidence(),
                readJson(recommendation.getWarningsJson(), STRING_LIST_TYPE),
                recommendation.getCreatedAt()
        );
    }

    private SessionAdjustmentContext buildSessionAdjustmentContext(FishingSession session, CreateSessionAdjustmentRequest request) {
        FishingSpot spot = session.getSpot();
        FishingPlan plan = session.getPlan();
        List<SessionLure> sessionLures = sessionLureRepository.findBySessionIdOrderByUsedFromAscIdAsc(session.getId());
        List<SessionEvent> events = sessionEventRepository.findBySessionIdOrderByEventTimeAscIdAsc(session.getId());

        List<SessionAdjustmentLure> allowedLures = sessionLures.stream()
                .map(sessionLure -> toSessionAdjustmentLure(sessionLure.getLure()))
                .toList();

        if (allowedLures.isEmpty() && plan != null) {
            allowedLures = fishingPlanLureRepository.findByPlanIdOrderByIdAsc(plan.getId())
                    .stream()
                    .map(FishingPlanLure::getLure)
                    .map(this::toSessionAdjustmentLure)
                    .toList();
        }

        return new SessionAdjustmentContext(
                request.situation(),
                request.currentConditions(),
                new SessionAdjustmentSession(
                        session.getId(),
                        session.getDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        sessionStatusOrDefault(session).name().toLowerCase(Locale.ROOT),
                        session.getTargetSpecies(),
                        session.getWaterClarity(),
                        session.getWaterLevel(),
                        session.getNotes(),
                        session.getSuccess(),
                        session.getDurationMinutes(),
                        session.getResultSummary(),
                        session.getFinalNotes(),
                        session.getRating()
                ),
                plan == null ? null : plan.getId(),
                new SessionAdjustmentSpot(
                        spot.getId(),
                        spot.getName(),
                        spot.getDescription(),
                        spot.getWaterType(),
                        spot.getFavoriteSpecies()
                ),
                weatherSnapshotRepository.findFirstBySessionIdOrderByCapturedAtDescIdDesc(session.getId())
                        .or(() -> plan == null ? java.util.Optional.empty() : weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(plan.getId()))
                        .map(this::toSessionAdjustmentWeather)
                        .orElse(null),
                allowedLures,
                events.stream()
                        .map(event -> new SessionAdjustmentEvent(event.getEventTime(), event.getEventType(), event.getDescription()))
                        .toList()
        );
    }

    private SessionAdjustmentLure toSessionAdjustmentLure(Lure lure) {
        LureLibraryItem libraryItem = lure.getLibraryItem();

        return new SessionAdjustmentLure(
                lure.getId(),
                lure.getName(),
                lure.getType(),
                lure.getColor(),
                lure.getSize(),
                lure.getWeight(),
                lure.getTargetSpecies(),
                lure.getWaterType(),
                libraryItem == null ? null : libraryItem.getName()
        );
    }

    private SessionAdjustmentWeather toSessionAdjustmentWeather(WeatherSnapshot weatherSnapshot) {
        return new SessionAdjustmentWeather(
                weatherSnapshot.getSource(),
                weatherSnapshot.getSourceLocationName(),
                weatherSnapshot.getForecastDate(),
                weatherSnapshot.getTemperatureMin(),
                weatherSnapshot.getTemperatureMax(),
                weatherSnapshot.getPrecipitationProbability(),
                weatherSnapshot.getWindDirection(),
                weatherSnapshot.getWindSpeedClass(),
                weatherSnapshot.getNotes()
        );
    }

    private Integer nextPlanVersion(Long planId) {
        return (int) aiRecommendationRepository.countByPlanIdAndRecommendationType(planId, PLAN_RECOMMENDATION) + 1;
    }

    private Integer nextSessionAdjustmentVersion(Long sessionId) {
        return (int) aiRecommendationRepository.countBySessionIdAndRecommendationType(sessionId, SESSION_ADJUSTMENT) + 1;
    }

    private void ensureWeatherSnapshotForPlan(FishingPlan plan) {
        if (weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(plan.getId()).isPresent()) {
            return;
        }

        try {
            weatherSnapshotService.createIpmaSnapshotForPlan(plan.getId());
        } catch (RuntimeException ignored) {
            // Weather improves the recommendation, but the AI planner should still work without IPMA.
        }
    }

    private FishingSessionStatus sessionStatusOrDefault(FishingSession session) {
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

    private String withFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not serialize AI recommendation data", ex);
        }
    }

    private <T> T readJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not deserialize AI recommendation data", ex);
        }
    }

    private <T> List<T> nullToEmpty(List<T> values) {
        return values == null ? List.of() : values;
    }

    private record SessionAdjustmentContext(
            String situation,
            String currentConditions,
            SessionAdjustmentSession session,
            Long planId,
            SessionAdjustmentSpot spot,
            SessionAdjustmentWeather weather,
            List<SessionAdjustmentLure> allowedLures,
            List<SessionAdjustmentEvent> events
    ) {
    }

    private record SessionAdjustmentSession(
            Long id,
            java.time.LocalDate date,
            java.time.LocalTime startTime,
            java.time.LocalTime endTime,
            String status,
            String targetSpecies,
            String waterClarity,
            String waterLevel,
            String notes,
            Boolean success,
            Long durationMinutes,
            String resultSummary,
            String finalNotes,
            Integer rating
    ) {
    }

    private record SessionAdjustmentSpot(
            Long id,
            String name,
            String description,
            String waterType,
            String favoriteSpecies
    ) {
    }

    private record SessionAdjustmentLure(
            Long id,
            String name,
            String type,
            String color,
            String size,
            Double weight,
            String targetSpecies,
            String waterType,
            String libraryItemName
    ) {
    }

    private record SessionAdjustmentWeather(
            String source,
            String sourceLocationName,
            java.time.LocalDate forecastDate,
            Double temperatureMin,
            Double temperatureMax,
            Double precipitationProbability,
            String windDirection,
            Integer windSpeedClass,
            String notes
    ) {
    }

    private record SessionAdjustmentEvent(
            java.time.LocalTime eventTime,
            String eventType,
            String description
    ) {
    }
}
