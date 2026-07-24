package com.lurepilot.backend.service;

import com.lurepilot.backend.client.LmStudioClient;
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

import java.util.List;

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
        AiPlanResult result = parsePlanResult(rawResponse);

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

    private String callLmStudio(String contextJson) {
        String systemMessage = """
                Es o LurePilot AI, um copiloto de pesca pratico.
                Responde sempre em portugues de Portugal.
                Usa apenas o contexto fornecido e conhecimento geral seguro.
                Nao inventes dados factuais, meteorologicos, legais ou historicos.
                Devolve apenas JSON valido, sem markdown.
                """;

        String userMessage = """
                Cria uma recomendacao de pesca em formato JSON com estes campos:
                summary: string curta.
                lureRanking: lista de objetos com rank, lure e reason.
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
                recommendation.getRawResponse(),
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
