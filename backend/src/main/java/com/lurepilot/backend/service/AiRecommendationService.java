package com.lurepilot.backend.service;

import com.lurepilot.backend.client.LmStudioClient;
import com.lurepilot.backend.dto.AiRecommendationDebugResponse;
import com.lurepilot.backend.dto.AiLureRankingResponse;
import com.lurepilot.backend.dto.AiPlanRecommendationResponse;
import com.lurepilot.backend.dto.AiPlanResult;
import com.lurepilot.backend.dto.AiSessionAdjustmentResponse;
import com.lurepilot.backend.dto.AiSessionAdjustmentResult;
import com.lurepilot.backend.dto.AiSessionReviewResponse;
import com.lurepilot.backend.dto.AiSessionReviewResult;
import com.lurepilot.backend.dto.CreateAiPlanRecommendationRequest;
import com.lurepilot.backend.dto.CreateSessionAdjustmentRequest;
import com.lurepilot.backend.dto.CreateSessionReviewRequest;
import com.lurepilot.backend.dto.PlannerContextResponse;
import com.lurepilot.backend.model.AiRecommendation;
import com.lurepilot.backend.model.Catch;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingPlanLure;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.model.RecommendationExecution;
import com.lurepilot.backend.model.SessionEvent;
import com.lurepilot.backend.model.SessionLure;
import com.lurepilot.backend.model.WeatherSnapshot;
import com.lurepilot.backend.repository.AiRecommendationRepository;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.FishingPlanLureRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.RecommendationExecutionRepository;
import com.lurepilot.backend.repository.SessionEventRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AiRecommendationService {

    private static final String PLAN_RECOMMENDATION = "PLAN";
    private static final String SESSION_ADJUSTMENT = "SESSION_ADJUSTMENT";
    private static final String SESSION_REVIEW = "SESSION_REVIEW";
    private static final int EXECUTION_HISTORY_LIMIT = 20;

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
    private final CatchRepository catchRepository;
    private final WeatherSnapshotRepository weatherSnapshotRepository;
    private final RecommendationExecutionRepository recommendationExecutionRepository;
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
            CatchRepository catchRepository,
            WeatherSnapshotRepository weatherSnapshotRepository,
            RecommendationExecutionRepository recommendationExecutionRepository,
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
        this.catchRepository = catchRepository;
        this.weatherSnapshotRepository = weatherSnapshotRepository;
        this.recommendationExecutionRepository = recommendationExecutionRepository;
        this.weatherSnapshotService = weatherSnapshotService;
        this.plannerContextService = plannerContextService;
        this.lmStudioClient = lmStudioClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AiPlanRecommendationResponse createPlanRecommendation(CreateAiPlanRecommendationRequest request) {
        FishingPlan plan = fishingPlanRepository.findById(request.planId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));

        ensureWeatherSnapshotForPlan(plan);
        PlannerContextResponse context = plannerContextService.buildContext(request.planId());
        String contextJson = writeJson(context);
        String rawResponse = callLmStudio(contextJson);
        AiPlanResult result = validatePlanResult(parsePlanResult(rawResponse), context);
        ConfidenceAssessment confidenceAssessment = assessPlanConfidence(result, context);
        result = applyConfidence(result, confidenceAssessment);

        supersedeLatestPlanRecommendations(plan.getId(), PLAN_RECOMMENDATION);

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
        applyConfidenceMetadata(recommendation, confidenceAssessment);

        return toResponse(aiRecommendationRepository.save(recommendation));
    }

    @Transactional
    public AiSessionAdjustmentResponse createSessionAdjustment(CreateSessionAdjustmentRequest request) {
        FishingSession session = fishingSessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));

        SessionAdjustmentContext context = buildSessionAdjustmentContext(session, request);
        String contextJson = writeJson(context);
        String rawResponse = callLmStudioForSessionAdjustment(contextJson);
        AiSessionAdjustmentResult result = validateSessionAdjustmentResult(parseSessionAdjustmentResult(rawResponse), context);
        ConfidenceAssessment confidenceAssessment = assessSessionAdjustmentConfidence(result, context);
        result = applyConfidence(result, confidenceAssessment);

        supersedeLatestSessionRecommendations(session.getId(), SESSION_ADJUSTMENT);

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
        applyConfidenceMetadata(recommendation, confidenceAssessment);

        return toSessionAdjustmentResponse(aiRecommendationRepository.save(recommendation));
    }

    @Transactional
    public AiSessionReviewResponse createSessionReview(CreateSessionReviewRequest request) {
        FishingSession session = fishingSessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));

        SessionReviewContext context = buildSessionReviewContext(session);
        String contextJson = writeJson(context);
        String rawResponse = callLmStudioForSessionReview(contextJson);
        AiSessionReviewResult result = validateSessionReviewResult(parseSessionReviewResult(rawResponse), context);
        ConfidenceAssessment confidenceAssessment = assessSessionReviewConfidence(result, context);
        result = applyConfidence(result, confidenceAssessment);

        supersedeLatestSessionRecommendations(session.getId(), SESSION_REVIEW);

        AiRecommendation recommendation = new AiRecommendation(
                session.getPlan(),
                session,
                SESSION_REVIEW,
                nextSessionReviewVersion(session.getId()),
                contextJson,
                rawResponse,
                result.summary(),
                writeJson(result.bestLure() == null || result.bestLure().isBlank()
                        ? List.of()
                        : List.of(new AiLureRankingResponse(1, result.bestLure(), result.bestLureReason()))),
                result.whatWorked(),
                result.whatFailed(),
                result.nextSessionSuggestion(),
                writeJson(nullToEmpty(result.keyLessons())),
                result.confidence(),
                writeJson(nullToEmpty(result.warnings()))
        );
        applyConfidenceMetadata(recommendation, confidenceAssessment);
        recommendation.setExtraJson(writeJson(result));

        return toSessionReviewResponse(aiRecommendationRepository.save(recommendation));
    }

    @Transactional(readOnly = true)
    public List<AiPlanRecommendationResponse> getRecommendationsByPlan(Long planId) {
        if (!fishingPlanRepository.existsById(planId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found");
        }

        return aiRecommendationRepository.findByPlanIdAndRecommendationTypeOrderByCreatedAtDescIdDesc(planId, PLAN_RECOMMENDATION)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AiPlanRecommendationResponse getLatestPlanRecommendation(Long planId) {
        if (!fishingPlanRepository.existsById(planId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found");
        }

        return aiRecommendationRepository.findFirstByPlanIdAndRecommendationTypeOrderByVersionDescIdDesc(planId, PLAN_RECOMMENDATION)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AI recommendation not found"));
    }

    @Transactional
    public AiPlanRecommendationResponse savePlanRecommendation(Long recommendationId) {
        AiRecommendation recommendation = aiRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AI recommendation not found"));

        if (!PLAN_RECOMMENDATION.equalsIgnoreCase(recommendation.getRecommendationType()) || recommendation.getPlan() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only plan recommendations can be saved from this endpoint");
        }

        recommendation.setSaved(true);
        return toResponse(aiRecommendationRepository.save(recommendation));
    }

    @Transactional(readOnly = true)
    public List<AiSessionAdjustmentResponse> getSessionAdjustments(Long sessionId) {
        if (!fishingSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        return aiRecommendationRepository.findBySessionIdAndRecommendationTypeOrderByCreatedAtDescIdDesc(sessionId, SESSION_ADJUSTMENT)
                .stream()
                .map(this::toSessionAdjustmentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AiSessionAdjustmentResponse getLatestSessionAdjustment(Long sessionId) {
        if (!fishingSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        return aiRecommendationRepository.findFirstBySessionIdAndRecommendationTypeOrderByVersionDescIdDesc(sessionId, SESSION_ADJUSTMENT)
                .map(this::toSessionAdjustmentResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AI session adjustment not found"));
    }

    @Transactional(readOnly = true)
    public List<AiSessionReviewResponse> getSessionReviews(Long sessionId) {
        if (!fishingSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        return aiRecommendationRepository.findBySessionIdAndRecommendationTypeOrderByCreatedAtDescIdDesc(sessionId, SESSION_REVIEW)
                .stream()
                .map(this::toSessionReviewResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AiSessionReviewResponse getLatestSessionReview(Long sessionId) {
        if (!fishingSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        return aiRecommendationRepository.findFirstBySessionIdAndRecommendationTypeOrderByVersionDescIdDesc(sessionId, SESSION_REVIEW)
                .map(this::toSessionReviewResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AI session review not found"));
    }

    public AiRecommendationDebugResponse getRecommendationDebug(Long id) {
        AiRecommendation recommendation = aiRecommendationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AI recommendation not found"));
        ResponseConfidence confidence = responseConfidence(recommendation);

        return new AiRecommendationDebugResponse(
                recommendation.getId(),
                recommendation.getPlan() == null ? null : recommendation.getPlan().getId(),
                recommendation.getSession() == null ? null : recommendation.getSession().getId(),
                recommendation.getRecommendationType(),
                recommendation.getVersion(),
                latestOrDefault(recommendation),
                confidence.score(),
                confidence.reason(),
                recommendation.getSupersededAt(),
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
                Usa a hora e data planeadas, o spot, as zonas do spot, as condicoes atuais e a previsao horaria do weather snapshot, o solunar forecast e os perfis das especies para adaptar a estrategia.
                Usa o solunar para considerar fase e iluminacao da Lua, nascer/por do Sol, nascer/por da Lua e janelas major/minor. Trata-o como indicador tradicional, nunca como garantia de captura.
                Usa targetSpeciesProfiles para considerar habitat, horas ativas, zona de ataque, zonas comuns e lures favoritas.
                Usa availableLibraryLures para conhecer tecnicas, dificuldade, eficacia, acao e condicoes ideais.
                Usa history para considerar sessoes anteriores e taxas de sucesso por spot e especie.
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
                Se o plano tiver varias especies alvo, considera-as em conjunto. Se indicar qualquer especie, usa o spot, weather e contexto geral sem inventar um alvo.

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

    private String callLmStudioForSessionReview(String contextJson) {
        String systemMessage = """
                Es o LurePilot AI, um copiloto de pesca focado em aprendizagem pos-sessao.
                Responde sempre em portugues de Portugal.
                Usa apenas o contexto fornecido e conhecimento geral seguro.
                Nao inventes capturas, eventos, lures, meteorologia ou historico.
                Se indicares bestLure, usa exclusivamente uma lure presente em usedLures.
                Devolve apenas JSON valido, sem markdown.
                """;

        String userMessage = """
                Analisa a sessao de pesca em formato JSON com estes campos:
                summary: string curta com o resumo da sessao.
                whatWorked: string sobre o que parece ter funcionado.
                whatFailed: string sobre o que nao resultou ou ficou fraco.
                bestLure: string ou null; se existir, tem de ser exatamente uma das lures em usedLures.
                bestLureReason: string curta ou null.
                observedPattern: string com padrao observado, sem inventar dados.
                nextSessionSuggestion: string com sugestao pratica para a proxima saida.
                keyLessons: lista de strings curtas.
                confidence: low, medium ou high.
                warnings: lista de strings quando houver poucos dados ou incerteza.

                Contexto estruturado:
                %s
                """.formatted(contextJson);

        try {
            return lmStudioClient.createChatCompletion(systemMessage, userMessage);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not get session review from LM Studio", ex);
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

    private AiSessionReviewResult parseSessionReviewResult(String rawResponse) {
        String json = extractJsonObject(rawResponse);

        try {
            return objectMapper.readValue(json, AiSessionReviewResult.class);
        } catch (Exception ex) {
            return new AiSessionReviewResult(
                    rawResponse,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of(),
                    "low",
                    List.of("O review foi guardado como resposta raw porque o JSON nao foi interpretado.")
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

        String planA = sanitizePlanText(
                "planA",
                result.planA(),
                "Comecar pela lure melhor classificada e pescar de forma controlada durante 20 minutos.",
                removedLures,
                warnings
        );
        String planB = sanitizePlanText(
                "planB",
                result.planB(),
                "Se nao houver sinais, alterar cadencia ou zona mantendo as lures selecionadas.",
                removedLures,
                warnings
        );
        String planC = sanitizePlanText(
                "planC",
                result.planC(),
                "Se continuar sem resultado, reduzir ritmo e focar estruturas ou zonas de sombra.",
                removedLures,
                warnings
        );

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

    private String sanitizePlanText(String fieldName, String value, String fallback, List<String> unavailableLures, List<String> warnings) {
        String text = withFallback(value, fallback);
        List<String> mentionedUnavailableLures = unavailableLures.stream()
                .filter(lure -> containsNormalized(text, lure))
                .toList();

        if (mentionedUnavailableLures.isEmpty()) {
            return text;
        }

        warnings.add("A IA mencionou lures fora de selectedLures em " + fieldName + " e o texto foi substituido: " + String.join(", ", mentionedUnavailableLures));
        return fallback;
    }

    private boolean containsNormalized(String text, String expected) {
        return !normalize(expected).isBlank() && normalize(text).contains(normalize(expected));
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

    private AiSessionReviewResult validateSessionReviewResult(AiSessionReviewResult result, SessionReviewContext context) {
        Set<String> usedLureNames = context.usedLures()
                .stream()
                .map(SessionReviewLure::name)
                .map(this::normalize)
                .collect(Collectors.toSet());

        List<String> warnings = new ArrayList<>(nullToEmpty(result.warnings()));
        String confidence = result.confidence();
        String bestLure = result.bestLure();
        String bestLureReason = result.bestLureReason();

        if (bestLure != null && !bestLure.isBlank() && !usedLureNames.contains(normalize(bestLure))) {
            warnings.add("A IA indicou uma melhor lure que nao esta em usedLures e o valor foi removido: " + bestLure);
            bestLure = null;
            bestLureReason = null;
            confidence = "low";
        }

        if (context.usedLures().isEmpty()) {
            warnings.add("A sessao nao tem lures registadas.");
            confidence = "low";
        }

        if (context.catches().isEmpty() && context.events().isEmpty()) {
            warnings.add("A sessao tem poucos dados registados para gerar conclusoes fortes.");
            confidence = "low";
        }

        return new AiSessionReviewResult(
                withFallback(result.summary(), "Resumo pos-sessao criado com base nos dados registados."),
                withFallback(result.whatWorked(), "Dados insuficientes para concluir com seguranca o que funcionou melhor."),
                withFallback(result.whatFailed(), "Dados insuficientes para concluir com seguranca o que falhou."),
                bestLure,
                bestLureReason,
                withFallback(result.observedPattern(), "Nao existe ainda um padrao claro com os dados desta sessao."),
                withFallback(result.nextSessionSuggestion(), "Na proxima sessao, registar lures, eventos e capturas com detalhe para melhorar a aprendizagem."),
                nullToEmpty(result.keyLessons()),
                normalizeConfidence(confidence),
                warnings
        );
    }

    private ConfidenceAssessment assessPlanConfidence(AiPlanResult result, PlannerContextResponse context) {
        int score = baseConfidenceScore(result.confidence());
        List<String> reasons = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        reasons.add("Modelo indicou " + normalizeConfidence(result.confidence()) + ".");

        if (context.weather() == null) {
            score -= 12;
            warnings.add("Sem weather snapshot no contexto do plano.");
        } else {
            score += 12;
            reasons.add("Weather snapshot incluido.");
        }

        if (context.solunar() == null) {
            score -= 6;
            warnings.add("Sem solunar forecast no contexto do plano.");
        } else {
            score += 6;
            reasons.add("Solunar forecast incluido.");
        }

        List<PlannerContextResponse.PlannerContextLure> selectedLures = nullToEmpty(context.selectedLures());
        int selectedLureCount = selectedLures.size();
        if (selectedLureCount == 0) {
            score -= 35;
            warnings.add("Plano sem lures selecionadas.");
        } else if (selectedLureCount == 1) {
            reasons.add("Apenas uma lure selecionada.");
        } else if (selectedLureCount == 2) {
            score += 8;
            reasons.add("Duas lures selecionadas.");
        } else {
            score += 14;
            reasons.add("Boa variedade de lures selecionadas.");
        }

        if (hasSpeciesMatch(context)) {
            score += 8;
            reasons.add("Ha lures alinhadas com a especie alvo.");
        } else if (selectedLureCount > 0) {
            score -= 8;
            warnings.add("Nenhuma lure selecionada indica explicitamente a especie alvo.");
        }

        if (hasWaterMatch(context)) {
            score += 5;
            reasons.add("Ha lures alinhadas com o tipo de agua do spot.");
        }

        int historicalCount = nullToEmpty(context.recentSpotSessions()).size() + nullToEmpty(context.recentSpeciesSessions()).size();
        if (historicalCount >= 5) {
            score += 14;
            reasons.add("Bom historico recente para spot/especie.");
        } else if (historicalCount >= 2) {
            score += 8;
            reasons.add("Algum historico recente disponivel.");
        } else if (historicalCount == 1) {
            score += 4;
            reasons.add("Historico recente ainda limitado.");
        } else {
            score -= 10;
            warnings.add("Sem sessoes recentes para comparar spot ou especie.");
        }

        long successfulSessions = countSuccessfulSessions(context);
        if (successfulSessions >= 2) {
            score += 8;
            reasons.add("Historico recente com sessoes bem sucedidas.");
        } else if (historicalCount > 0 && successfulSessions == 0) {
            score -= 5;
            reasons.add("Historico recente sem resultados positivos registados.");
        }

        ExecutionHistoryImpact executionHistoryImpact = assessExecutionHistory(
                "para este plano/spot/especie",
                buildPlanExecutionStats(context)
        );
        score += executionHistoryImpact.scoreDelta();
        reasons.addAll(executionHistoryImpact.reasons());
        warnings.addAll(executionHistoryImpact.warnings());

        if (context.dataQuality() != null && !nullToEmpty(context.dataQuality().warnings()).isEmpty()) {
            int penalty = Math.min(nullToEmpty(context.dataQuality().warnings()).size() * 4, 12);
            score -= penalty;
            warnings.addAll(nullToEmpty(context.dataQuality().warnings()));
        }

        if (nullToEmpty(result.lureRanking()).isEmpty()) {
            score -= 25;
        }

        if (!nullToEmpty(result.warnings()).isEmpty()) {
            score -= Math.min(nullToEmpty(result.warnings()).size() * 5, 20);
        }

        return finalizeConfidence(score, reasons, warnings, result.confidence());
    }

    private ConfidenceAssessment assessSessionAdjustmentConfidence(AiSessionAdjustmentResult result, SessionAdjustmentContext context) {
        int score = baseConfidenceScore(result.confidence());
        List<String> reasons = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        reasons.add("Modelo indicou " + normalizeConfidence(result.confidence()) + ".");

        if (context.weather() == null) {
            score -= 8;
            warnings.add("Sem weather snapshot recente para a sessao/plano.");
        } else {
            score += 8;
            reasons.add("Weather snapshot incluido.");
        }

        if (nullToEmpty(context.allowedLures()).isEmpty()) {
            score -= 35;
            warnings.add("Sessao sem lures permitidas no contexto.");
        } else if (context.allowedLures().size() >= 3) {
            score += 12;
            reasons.add("Boa variedade de lures disponiveis para ajuste.");
        } else {
            score += 4;
            reasons.add("Poucas lures disponiveis para ajuste.");
        }

        if (nullToEmpty(context.events()).isEmpty()) {
            score -= 8;
            warnings.add("Sessao sem eventos registados para orientar o ajuste.");
        } else if (context.events().size() >= 2) {
            score += 8;
            reasons.add("Eventos da sessao ajudam a ajustar estrategia.");
        }

        if (context.situation() == null || context.situation().isBlank()) {
            score -= 10;
            warnings.add("Situacao atual nao foi descrita.");
        } else {
            score += 10;
            reasons.add("Situacao atual descrita pelo utilizador.");
        }

        if (context.currentConditions() == null || context.currentConditions().isBlank()) {
            score -= 4;
        } else {
            score += 4;
            reasons.add("Condicoes atuais descritas pelo utilizador.");
        }

        if ("active".equalsIgnoreCase(context.session().status())) {
            score += 6;
            reasons.add("Sessao esta ativa.");
        }

        ExecutionHistoryImpact executionHistoryImpact = assessExecutionHistory(
                "para ajustes desta sessao/plano",
                buildSessionExecutionStats(context.session().id(), context.planId(), SESSION_ADJUSTMENT)
        );
        score += executionHistoryImpact.scoreDelta();
        reasons.addAll(executionHistoryImpact.reasons());
        warnings.addAll(executionHistoryImpact.warnings());

        if (nullToEmpty(result.lureRanking()).isEmpty()) {
            score -= 25;
        }

        if (!nullToEmpty(result.warnings()).isEmpty()) {
            score -= Math.min(nullToEmpty(result.warnings()).size() * 5, 20);
        }

        return finalizeConfidence(score, reasons, warnings, result.confidence());
    }

    private ConfidenceAssessment assessSessionReviewConfidence(AiSessionReviewResult result, SessionReviewContext context) {
        int score = baseConfidenceScore(result.confidence());
        List<String> reasons = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        reasons.add("Modelo indicou " + normalizeConfidence(result.confidence()) + ".");

        if (nullToEmpty(context.usedLures()).isEmpty()) {
            score -= 30;
            warnings.add("Review sem lures usadas registadas.");
        } else {
            score += Math.min(context.usedLures().size() * 4, 12);
            reasons.add("Lures usadas registadas.");
        }

        if (nullToEmpty(context.catches()).isEmpty()) {
            score -= 10;
            reasons.add("Sem capturas registadas.");
        } else {
            score += 14;
            reasons.add("Capturas registadas ajudam a validar o padrao.");
        }

        if (nullToEmpty(context.events()).isEmpty()) {
            score -= 8;
            warnings.add("Sem eventos da sessao registados.");
        } else {
            score += Math.min(context.events().size() * 3, 9);
            reasons.add("Eventos da sessao incluidos.");
        }

        if (context.weather() == null) {
            score -= 6;
            warnings.add("Sem weather snapshot para comparar condicoes.");
        } else {
            score += 6;
            reasons.add("Weather snapshot incluido.");
        }

        if ("finished".equalsIgnoreCase(context.session().status())) {
            score += 8;
            reasons.add("Sessao finalizada.");
        } else {
            score -= 8;
            warnings.add("Sessao ainda nao esta finalizada.");
        }

        if (context.session().rating() != null) {
            score += 5;
            reasons.add("Sessao tem avaliacao do utilizador.");
        }

        ExecutionHistoryImpact executionHistoryImpact = assessExecutionHistory(
                "da sessao revista",
                buildSessionReviewExecutionStats(context.session().id())
        );
        score += executionHistoryImpact.scoreDelta();
        reasons.addAll(executionHistoryImpact.reasons());
        warnings.addAll(executionHistoryImpact.warnings());

        if (!nullToEmpty(result.warnings()).isEmpty()) {
            score -= Math.min(nullToEmpty(result.warnings()).size() * 5, 20);
        }

        return finalizeConfidence(score, reasons, warnings, result.confidence());
    }

    private AiPlanResult applyConfidence(AiPlanResult result, ConfidenceAssessment confidenceAssessment) {
        return new AiPlanResult(
                result.summary(),
                nullToEmpty(result.lureRanking()),
                result.planA(),
                result.planB(),
                result.planC(),
                nullToEmpty(result.avoid()),
                confidenceAssessment.confidence(),
                mergeWarnings(result.warnings(), confidenceAssessment.warnings())
        );
    }

    private AiSessionAdjustmentResult applyConfidence(AiSessionAdjustmentResult result, ConfidenceAssessment confidenceAssessment) {
        return new AiSessionAdjustmentResult(
                result.summary(),
                nullToEmpty(result.lureRanking()),
                result.immediateAction(),
                result.nextTechnique(),
                result.fallbackAction(),
                nullToEmpty(result.avoid()),
                confidenceAssessment.confidence(),
                mergeWarnings(result.warnings(), confidenceAssessment.warnings())
        );
    }

    private AiSessionReviewResult applyConfidence(AiSessionReviewResult result, ConfidenceAssessment confidenceAssessment) {
        return new AiSessionReviewResult(
                result.summary(),
                result.whatWorked(),
                result.whatFailed(),
                result.bestLure(),
                result.bestLureReason(),
                result.observedPattern(),
                result.nextSessionSuggestion(),
                nullToEmpty(result.keyLessons()),
                confidenceAssessment.confidence(),
                mergeWarnings(result.warnings(), confidenceAssessment.warnings())
        );
    }

    private void applyConfidenceMetadata(AiRecommendation recommendation, ConfidenceAssessment confidenceAssessment) {
        recommendation.setLatest(true);
        recommendation.setConfidenceScore(confidenceAssessment.score());
        recommendation.setConfidenceReason(confidenceAssessment.reason());
    }

    private ConfidenceAssessment finalizeConfidence(int score, List<String> reasons, List<String> warnings, String modelConfidence) {
        int cappedScore = Math.max(0, Math.min(100, score));
        String normalizedModelConfidence = normalizeConfidence(modelConfidence);
        if ("low".equals(normalizedModelConfidence)) {
            cappedScore = Math.min(cappedScore, 44);
        }

        String confidence = confidenceFromScore(cappedScore);
        String reason = "Score calculado " + cappedScore + "/100: " + String.join(" ", reasons);

        return new ConfidenceAssessment(confidence, cappedScore, reason, warnings);
    }

    private int baseConfidenceScore(String confidence) {
        return switch (normalizeConfidence(confidence)) {
            case "high" -> 70;
            case "medium" -> 55;
            default -> 35;
        };
    }

    private String confidenceFromScore(int score) {
        if (score >= 75) {
            return "high";
        }

        if (score >= 45) {
            return "medium";
        }

        return "low";
    }

    private ExecutionHistoryStats buildPlanExecutionStats(PlannerContextResponse context) {
        List<RecommendationExecution> executions = new ArrayList<>();
        Set<Long> seenIds = new LinkedHashSet<>();

        Long planId = context.plan() == null ? null : context.plan().id();
        if (planId != null) {
            addUniqueExecutions(
                    executions,
                    seenIds,
                    recommendationExecutionRepository.findRecentByPlanAndType(
                            planId,
                            PLAN_RECOMMENDATION,
                            PageRequest.of(0, EXECUTION_HISTORY_LIMIT)
                    )
            );
        }

        Long spotId = context.spot() == null ? null : context.spot().id();
        String targetSpecies = context.plan() == null ? null : context.plan().targetSpecies();
        if (spotId != null && targetSpecies != null && !targetSpecies.isBlank()) {
            addUniqueExecutions(
                    executions,
                    seenIds,
                    recommendationExecutionRepository.findRecentBySpotSpeciesAndType(
                            spotId,
                            targetSpecies,
                            PLAN_RECOMMENDATION,
                            PageRequest.of(0, EXECUTION_HISTORY_LIMIT)
                    )
            );
        }

        return summarizeExecutionHistory(executions);
    }

    private ExecutionHistoryStats buildSessionExecutionStats(Long sessionId, Long planId, String recommendationType) {
        List<RecommendationExecution> executions = new ArrayList<>();
        Set<Long> seenIds = new LinkedHashSet<>();

        if (sessionId != null) {
            addUniqueExecutions(
                    executions,
                    seenIds,
                    recommendationExecutionRepository.findRecentBySessionAndType(
                            sessionId,
                            recommendationType,
                            PageRequest.of(0, EXECUTION_HISTORY_LIMIT)
                    )
            );
        }

        if (planId != null) {
            addUniqueExecutions(
                    executions,
                    seenIds,
                    recommendationExecutionRepository.findRecentByPlanAndType(
                            planId,
                            recommendationType,
                            PageRequest.of(0, EXECUTION_HISTORY_LIMIT)
                    )
            );
        }

        return summarizeExecutionHistory(executions);
    }

    private ExecutionHistoryStats buildSessionReviewExecutionStats(Long sessionId) {
        if (sessionId == null) {
            return ExecutionHistoryStats.empty();
        }

        return summarizeExecutionHistory(recommendationExecutionRepository.findBySessionIdOrderByCreatedAtDescIdDesc(sessionId));
    }

    private void addUniqueExecutions(List<RecommendationExecution> executions, Set<Long> seenIds, List<RecommendationExecution> source) {
        for (RecommendationExecution execution : nullToEmpty(source)) {
            Long id = execution.getId();
            if (id == null || seenIds.add(id)) {
                executions.add(execution);
            }
        }
    }

    private ExecutionHistoryStats summarizeExecutionHistory(List<RecommendationExecution> executions) {
        int totalCount = 0;
        int followedCount = 0;
        int successfulFollowedCount = 0;
        int failedFollowedCount = 0;
        int ratingCount = 0;
        int ratingSum = 0;

        for (RecommendationExecution execution : nullToEmpty(executions)) {
            totalCount++;
            boolean followed = Boolean.TRUE.equals(execution.getFollowed());
            boolean success = Boolean.TRUE.equals(execution.getSuccess());

            if (followed) {
                followedCount++;
                if (success) {
                    successfulFollowedCount++;
                } else if (Boolean.FALSE.equals(execution.getSuccess())) {
                    failedFollowedCount++;
                }
            }

            if (execution.getRating() != null) {
                ratingCount++;
                ratingSum += execution.getRating();
            }
        }

        Double averageRating = ratingCount == 0 ? null : (double) ratingSum / ratingCount;
        return new ExecutionHistoryStats(totalCount, followedCount, successfulFollowedCount, failedFollowedCount, averageRating);
    }

    private ExecutionHistoryImpact assessExecutionHistory(String label, ExecutionHistoryStats stats) {
        if (stats.totalCount() == 0) {
            return new ExecutionHistoryImpact(0, List.of(), List.of());
        }

        int scoreDelta = 0;
        List<String> reasons = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        reasons.add("Historico de execucao " + label + ": "
                + stats.followedCount() + "/" + stats.totalCount()
                + " recomendacoes marcadas como seguidas.");

        if (stats.followedCount() == 0) {
            warnings.add("Existem recomendacoes anteriores, mas ainda nenhuma foi marcada como seguida.");
            return new ExecutionHistoryImpact(-2, reasons, warnings);
        }

        double successRate = (double) stats.successfulFollowedCount() / stats.followedCount() * 100;
        if (stats.followedCount() >= 3) {
            if (successRate >= 70) {
                scoreDelta += 12;
                reasons.add("Quando seguidas, recomendacoes semelhantes tiveram bons resultados (" + roundedPercent(successRate) + "%).");
            } else if (successRate >= 45) {
                scoreDelta += 5;
                reasons.add("Quando seguidas, recomendacoes semelhantes tiveram resultado misto (" + roundedPercent(successRate) + "%).");
            } else {
                scoreDelta -= 12;
                warnings.add("Quando seguidas, recomendacoes semelhantes tiveram poucos resultados positivos (" + roundedPercent(successRate) + "%).");
            }
        } else if (stats.successfulFollowedCount() > 0) {
            scoreDelta += 4;
            reasons.add("Existe algum feedback positivo de execucao.");
        } else if (stats.failedFollowedCount() > 0) {
            scoreDelta -= 4;
            warnings.add("Existe algum feedback negativo de execucao.");
        }

        if (stats.averageRating() != null) {
            if (stats.averageRating() >= 4.0) {
                scoreDelta += 5;
                reasons.add("Avaliacoes anteriores foram fortes.");
            } else if (stats.averageRating() <= 2.0) {
                scoreDelta -= 5;
                warnings.add("Avaliacoes anteriores foram fracas.");
            }
        }

        return new ExecutionHistoryImpact(scoreDelta, reasons, warnings);
    }

    private ResponseConfidence responseConfidence(AiRecommendation recommendation) {
        int storedScore = recommendation.getConfidenceScore() == null
                ? baseConfidenceScore(recommendation.getConfidence())
                : recommendation.getConfidenceScore();
        String storedConfidence = recommendation.getConfidence() == null || recommendation.getConfidence().isBlank()
                ? confidenceFromScore(storedScore)
                : normalizeConfidence(recommendation.getConfidence());
        String storedReason = recommendation.getConfidenceReason();

        if (recommendation.getId() == null) {
            return new ResponseConfidence(storedConfidence, storedScore, storedReason);
        }

        ExecutionHistoryStats stats = summarizeExecutionHistory(
                recommendationExecutionRepository.findByRecommendationIdOrderByCreatedAtDescIdDesc(recommendation.getId())
        );
        if (stats.totalCount() == 0) {
            return new ResponseConfidence(storedConfidence, storedScore, storedReason);
        }

        ExecutionHistoryImpact impact = assessExecutionHistory("desta recomendacao", stats);
        int score = Math.max(0, Math.min(100, storedScore + impact.scoreDelta()));
        String reason = appendExecutionFeedback(storedReason, stats);

        return new ResponseConfidence(confidenceFromScore(score), score, reason);
    }

    private String appendExecutionFeedback(String reason, ExecutionHistoryStats stats) {
        String feedback = "Feedback de execucao: " + stats.followedCount() + "/" + stats.totalCount()
                + " execucoes marcadas como seguidas";

        if (stats.followedCount() > 0) {
            double successRate = (double) stats.successfulFollowedCount() / stats.followedCount() * 100;
            feedback += ", sucesso quando seguida " + roundedPercent(successRate) + "%";
        }

        if (stats.averageRating() != null) {
            feedback += ", avaliacao media " + String.format(Locale.ROOT, "%.1f", stats.averageRating());
        }

        feedback += ".";
        return reason == null || reason.isBlank() ? feedback : reason + " " + feedback;
    }

    private int roundedPercent(double value) {
        return (int) Math.round(value);
    }

    private boolean hasSpeciesMatch(PlannerContextResponse context) {
        String targetSpecies = normalize(context.plan().targetSpecies());
        if (targetSpecies.isBlank()) {
            return false;
        }

        return nullToEmpty(context.selectedLures())
                .stream()
                .anyMatch(lure -> containsNormalized(lure.targetSpecies(), targetSpecies));
    }

    private boolean hasWaterMatch(PlannerContextResponse context) {
        String spotWaterType = context.spot() == null ? "" : normalize(context.spot().waterType());
        if (spotWaterType.isBlank()) {
            return false;
        }

        return nullToEmpty(context.selectedLures())
                .stream()
                .anyMatch(lure -> normalize(lure.waterType()).equals(spotWaterType));
    }

    private long countSuccessfulSessions(PlannerContextResponse context) {
        return java.util.stream.Stream.concat(
                        nullToEmpty(context.recentSpotSessions()).stream(),
                        nullToEmpty(context.recentSpeciesSessions()).stream()
                )
                .filter(session -> Boolean.TRUE.equals(session.success()))
                .count();
    }

    private List<String> mergeWarnings(List<String> primaryWarnings, List<String> extraWarnings) {
        LinkedHashSet<String> warnings = new LinkedHashSet<>();
        warnings.addAll(nullToEmpty(primaryWarnings));
        warnings.addAll(nullToEmpty(extraWarnings));
        return List.copyOf(warnings);
    }

    private void supersedeLatestPlanRecommendations(Long planId, String recommendationType) {
        List<AiRecommendation> latestRecommendations = aiRecommendationRepository.findLatestByPlanIdAndRecommendationType(planId, recommendationType);
        supersede(latestRecommendations);
    }

    private void supersedeLatestSessionRecommendations(Long sessionId, String recommendationType) {
        List<AiRecommendation> latestRecommendations = aiRecommendationRepository.findLatestBySessionIdAndRecommendationType(sessionId, recommendationType);
        supersede(latestRecommendations);
    }

    private void supersede(List<AiRecommendation> recommendations) {
        if (recommendations.isEmpty()) {
            return;
        }

        Instant supersededAt = Instant.now();
        for (AiRecommendation recommendation : recommendations) {
            recommendation.setLatest(false);
            recommendation.setSupersededAt(supersededAt);
        }
        aiRecommendationRepository.saveAll(recommendations);
    }

    private Boolean latestOrDefault(AiRecommendation recommendation) {
        return recommendation.getLatest() == null || recommendation.getLatest();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeConfidence(String value) {
        String normalized = normalize(value);
        if (Set.of("low", "medium", "high").contains(normalized)) {
            return normalized;
        }

        return "low";
    }

    private AiPlanRecommendationResponse toResponse(AiRecommendation recommendation) {
        ResponseConfidence confidence = responseConfidence(recommendation);

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
                confidence.confidence(),
                confidence.score(),
                confidence.reason(),
                latestOrDefault(recommendation),
                Boolean.TRUE.equals(recommendation.getSaved()),
                readJson(recommendation.getWarningsJson(), STRING_LIST_TYPE),
                recommendation.getCreatedAt()
        );
    }

    private AiSessionAdjustmentResponse toSessionAdjustmentResponse(AiRecommendation recommendation) {
        ResponseConfidence confidence = responseConfidence(recommendation);

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
                confidence.confidence(),
                confidence.score(),
                confidence.reason(),
                latestOrDefault(recommendation),
                readJson(recommendation.getWarningsJson(), STRING_LIST_TYPE),
                recommendation.getCreatedAt()
        );
    }

    private AiSessionReviewResponse toSessionReviewResponse(AiRecommendation recommendation) {
        ResponseConfidence confidence = responseConfidence(recommendation);
        AiSessionReviewResult result = recommendation.getExtraJson() == null
                ? new AiSessionReviewResult(
                recommendation.getSummary(),
                recommendation.getPlanA(),
                recommendation.getPlanB(),
                null,
                null,
                null,
                recommendation.getPlanC(),
                readJson(recommendation.getAvoidJson(), STRING_LIST_TYPE),
                recommendation.getConfidence(),
                readJson(recommendation.getWarningsJson(), STRING_LIST_TYPE)
        )
                : readJson(recommendation.getExtraJson(), AiSessionReviewResult.class);

        return new AiSessionReviewResponse(
                recommendation.getId(),
                recommendation.getSession().getId(),
                recommendation.getPlan() == null ? null : recommendation.getPlan().getId(),
                recommendation.getVersion(),
                result.summary(),
                result.whatWorked(),
                result.whatFailed(),
                result.bestLure(),
                result.bestLureReason(),
                result.observedPattern(),
                result.nextSessionSuggestion(),
                nullToEmpty(result.keyLessons()),
                confidence.confidence(),
                confidence.score(),
                confidence.reason(),
                latestOrDefault(recommendation),
                nullToEmpty(result.warnings()),
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

    private SessionReviewContext buildSessionReviewContext(FishingSession session) {
        FishingSpot spot = session.getSpot();
        FishingPlan plan = session.getPlan();
        List<SessionLure> sessionLures = sessionLureRepository.findBySessionIdOrderByUsedFromAscIdAsc(session.getId());
        List<SessionEvent> events = sessionEventRepository.findBySessionIdOrderByEventTimeAscIdAsc(session.getId());
        List<Catch> catches = catchRepository.findBySessionIdOrderByIdAsc(session.getId());

        return new SessionReviewContext(
                new SessionReviewSession(
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
                sessionLures.stream()
                        .map(this::toSessionReviewLure)
                        .toList(),
                events.stream()
                        .map(event -> new SessionAdjustmentEvent(event.getEventTime(), event.getEventType(), event.getDescription()))
                        .toList(),
                catches.stream()
                        .map(this::toSessionReviewCatch)
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

    private SessionReviewLure toSessionReviewLure(SessionLure sessionLure) {
        Lure lure = sessionLure.getLure();

        return new SessionReviewLure(
                lure.getId(),
                lure.getName(),
                lure.getType(),
                lure.getColor(),
                sessionLure.getUsedFrom(),
                sessionLure.getUsedTo(),
                sessionLure.getResultNotes()
        );
    }

    private SessionReviewCatch toSessionReviewCatch(Catch catchRecord) {
        return new SessionReviewCatch(
                catchRecord.getSpecies(),
                catchRecord.getQuantity(),
                catchRecord.getSizeCm(),
                catchRecord.getWeightKg(),
                catchRecord.getReleased(),
                catchRecord.getNotes()
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

    private Integer nextSessionReviewVersion(Long sessionId) {
        return (int) aiRecommendationRepository.countBySessionIdAndRecommendationType(sessionId, SESSION_REVIEW) + 1;
    }

    private void ensureWeatherSnapshotForPlan(FishingPlan plan) {
        if (weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(plan.getId()).isPresent()) {
            return;
        }

        try {
            weatherSnapshotService.createSnapshotForPlan(plan.getId());
        } catch (RuntimeException ignored) {
            // Weather improves the recommendation, but the AI planner should still work without an external provider.
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

    private <T> T readJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not deserialize AI recommendation data", ex);
        }
    }

    private <T> List<T> nullToEmpty(List<T> values) {
        return values == null ? List.of() : values;
    }

    private record ConfidenceAssessment(
            String confidence,
            Integer score,
            String reason,
            List<String> warnings
    ) {
    }

    private record ResponseConfidence(
            String confidence,
            Integer score,
            String reason
    ) {
    }

    private record ExecutionHistoryStats(
            int totalCount,
            int followedCount,
            int successfulFollowedCount,
            int failedFollowedCount,
            Double averageRating
    ) {
        private static ExecutionHistoryStats empty() {
            return new ExecutionHistoryStats(0, 0, 0, 0, null);
        }
    }

    private record ExecutionHistoryImpact(
            int scoreDelta,
            List<String> reasons,
            List<String> warnings
    ) {
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

    private record SessionReviewContext(
            SessionReviewSession session,
            Long planId,
            SessionAdjustmentSpot spot,
            SessionAdjustmentWeather weather,
            List<SessionReviewLure> usedLures,
            List<SessionAdjustmentEvent> events,
            List<SessionReviewCatch> catches
    ) {
    }

    private record SessionReviewSession(
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

    private record SessionReviewLure(
            Long id,
            String name,
            String type,
            String color,
            java.time.LocalTime usedFrom,
            java.time.LocalTime usedTo,
            String resultNotes
    ) {
    }

    private record SessionReviewCatch(
            String species,
            Integer quantity,
            Double sizeCm,
            Double weightKg,
            Boolean released,
            String notes
    ) {
    }
}
