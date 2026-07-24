package com.lurepilot.backend.service;

import com.lurepilot.backend.client.LmStudioClient;
import com.lurepilot.backend.dto.AiRecommendationDebugResponse;
import com.lurepilot.backend.dto.AiLureRankingResponse;
import com.lurepilot.backend.dto.AiPlanRecommendationResponse;
import com.lurepilot.backend.dto.AiPlanResult;
import com.lurepilot.backend.dto.CreateAiPlanRecommendationRequest;
import com.lurepilot.backend.dto.PlannerContextResponse;
import com.lurepilot.backend.model.AiRecommendation;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.repository.AiRecommendationRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
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

    private static final TypeReference<List<AiLureRankingResponse>> LURE_RANKING_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    private final AiRecommendationRepository aiRecommendationRepository;
    private final FishingPlanRepository fishingPlanRepository;
    private final PlannerContextService plannerContextService;
    private final LmStudioClient lmStudioClient;
    private final ObjectMapper objectMapper;

    public AiRecommendationService(
            AiRecommendationRepository aiRecommendationRepository,
            FishingPlanRepository fishingPlanRepository,
            PlannerContextService plannerContextService,
            LmStudioClient lmStudioClient,
            ObjectMapper objectMapper
    ) {
        this.aiRecommendationRepository = aiRecommendationRepository;
        this.fishingPlanRepository = fishingPlanRepository;
        this.plannerContextService = plannerContextService;
        this.lmStudioClient = lmStudioClient;
        this.objectMapper = objectMapper;
    }

    public AiPlanRecommendationResponse createPlanRecommendation(CreateAiPlanRecommendationRequest request) {
        FishingPlan plan = fishingPlanRepository.findById(request.planId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));

        PlannerContextResponse context = plannerContextService.buildContext(request.planId());
        String contextJson = writeJson(context);
        String rawResponse = callLmStudio(contextJson);
        AiPlanResult result = validatePlanResult(parsePlanResult(rawResponse), context);

        AiRecommendation recommendation = new AiRecommendation(
                plan,
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
        if (!removedLures.isEmpty()) {
            confidence = "low";
        }

        return new AiPlanResult(
                result.summary(),
                validatedRanking,
                result.planA(),
                result.planB(),
                result.planC(),
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
}
