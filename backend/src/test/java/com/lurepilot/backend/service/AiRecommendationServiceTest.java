package com.lurepilot.backend.service;

import com.lurepilot.backend.client.LmStudioClient;
import com.lurepilot.backend.dto.AiPlanRecommendationResponse;
import com.lurepilot.backend.dto.CreateAiPlanRecommendationRequest;
import com.lurepilot.backend.dto.PlannerContextResponse;
import com.lurepilot.backend.model.AiRecommendation;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.RecommendationExecution;
import com.lurepilot.backend.repository.AiRecommendationRepository;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.FishingPlanLureRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.RecommendationExecutionRepository;
import com.lurepilot.backend.repository.SessionEventRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiRecommendationServiceTest {

    @Mock
    private AiRecommendationRepository aiRecommendationRepository;

    @Mock
    private FishingPlanRepository fishingPlanRepository;

    @Mock
    private FishingSessionRepository fishingSessionRepository;

    @Mock
    private FishingPlanLureRepository fishingPlanLureRepository;

    @Mock
    private SessionLureRepository sessionLureRepository;

    @Mock
    private SessionEventRepository sessionEventRepository;

    @Mock
    private CatchRepository catchRepository;

    @Mock
    private WeatherSnapshotRepository weatherSnapshotRepository;

    @Mock
    private RecommendationExecutionRepository recommendationExecutionRepository;

    @Mock
    private WeatherSnapshotService weatherSnapshotService;

    @Mock
    private PlannerContextService plannerContextService;

    @Mock
    private LmStudioClient lmStudioClient;

    private AiRecommendationService aiRecommendationService;

    @BeforeEach
    void setUp() {
        aiRecommendationService = new AiRecommendationService(
                aiRecommendationRepository,
                fishingPlanRepository,
                fishingSessionRepository,
                fishingPlanLureRepository,
                sessionLureRepository,
                sessionEventRepository,
                catchRepository,
                weatherSnapshotRepository,
                recommendationExecutionRepository,
                weatherSnapshotService,
                plannerContextService,
                lmStudioClient,
                new ObjectMapper()
        );
    }

    @Test
    void createPlanRecommendationRemovesLuresOutsideSelectedContext() {
        FishingSpot spot = new FishingSpot("Barragem Norte", null, 38.7, -9.1, "DAM", "BASS");
        ReflectionTestUtils.setField(spot, "id", 10L);
        FishingPlan plan = new FishingPlan(spot, LocalDate.of(2026, 7, 24), LocalTime.of(19, 0), "BASS", "CLEAR", "LOW", null);
        ReflectionTestUtils.setField(plan, "id", 1L);

        PlannerContextResponse context = new PlannerContextResponse(
                new PlannerContextResponse.PlannerContextPlan(1L, LocalDate.of(2026, 7, 24), LocalTime.of(19, 0), "BASS", "CLEAR", "LOW", null),
                new PlannerContextResponse.PlannerContextSpot(10L, "Barragem Norte", null, 38.7, -9.1, "DAM", "BASS"),
                null,
                List.of(new PlannerContextResponse.PlannerContextLure(100L, "Vinil verde natural", "SOFT_BAIT", "GREEN", "10cm", 7.0, "Generic", "BASS", "DAM", null, null)),
                List.of(),
                List.of(),
                new PlannerContextResponse.PlannerContextDataQuality(1, 0, 0, "medium", List.of())
        );
        String rawResponse = """
                {
                  "summary": "Plano simples.",
                  "lureRanking": [
                    {"rank": 1, "lure": "Spinnerbait inventado", "reason": "Nao esta no contexto."},
                    {"rank": 2, "lure": "Vinil verde natural", "reason": "Esta no contexto."}
                  ],
                  "planA": "Comecar com Spinnerbait inventado junto a estrutura.",
                  "planB": "Mudar cadencia.",
                  "planC": "Procurar sombra.",
                  "avoid": [],
                  "confidence": "high",
                  "warnings": []
                }
                """;

        when(fishingPlanRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(1L)).thenReturn(Optional.empty());
        when(plannerContextService.buildContext(1L)).thenReturn(context);
        when(lmStudioClient.createChatCompletion(anyString(), anyString())).thenReturn(rawResponse);
        when(aiRecommendationRepository.countByPlanIdAndRecommendationType(1L, "PLAN")).thenReturn(0L);
        when(aiRecommendationRepository.save(any(AiRecommendation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AiPlanRecommendationResponse response = aiRecommendationService.createPlanRecommendation(new CreateAiPlanRecommendationRequest(1L));

        assertThat(response.version()).isEqualTo(1);
        assertThat(response.confidence()).isEqualTo("low");
        assertThat(response.lureRanking()).hasSize(1);
        assertThat(response.lureRanking().getFirst().rank()).isEqualTo(1);
        assertThat(response.lureRanking().getFirst().lure()).isEqualTo("Vinil verde natural");
        assertThat(response.planA()).isEqualTo("Comecar pela lure melhor classificada e pescar de forma controlada durante 20 minutos.");
        assertThat(response.warnings()).anySatisfy(warning -> assertThat(warning).contains("Spinnerbait inventado"));
        assertThat(response.warnings()).anySatisfy(warning -> assertThat(warning).contains("planA"));
        verify(weatherSnapshotService).createSnapshotForPlan(1L);
    }

    @Test
    void createPlanRecommendationUsesExecutionHistoryInConfidenceReason() {
        FishingSpot spot = new FishingSpot("Barragem Norte", null, 38.7, -9.1, "DAM", "BASS");
        ReflectionTestUtils.setField(spot, "id", 10L);
        FishingPlan plan = new FishingPlan(spot, LocalDate.of(2026, 7, 24), LocalTime.of(19, 0), "BASS", "CLEAR", "LOW", null);
        ReflectionTestUtils.setField(plan, "id", 1L);

        PlannerContextResponse context = new PlannerContextResponse(
                new PlannerContextResponse.PlannerContextPlan(1L, LocalDate.of(2026, 7, 24), LocalTime.of(19, 0), "BASS", "CLEAR", "LOW", null),
                new PlannerContextResponse.PlannerContextSpot(10L, "Barragem Norte", null, 38.7, -9.1, "DAM", "BASS"),
                new PlannerContextResponse.PlannerContextWeather(1L, "OPEN_METEO", "Lisboa", LocalDate.of(2026, 7, 24), 18.0, 27.0, 0.0, "NW", 2, null),
                List.of(
                        new PlannerContextResponse.PlannerContextLure(100L, "Vinil verde natural", "SOFT_BAIT", "GREEN", "10cm", 7.0, "Generic", "BASS", "DAM", null, null),
                        new PlannerContextResponse.PlannerContextLure(101L, "Crankbait pequeno natural", "CRANKBAIT", "NATURAL", "5cm", 9.0, "Generic", "BASS", "DAM", null, null),
                        new PlannerContextResponse.PlannerContextLure(102L, "Popper branco", "TOPWATER", "WHITE", "6cm", 10.0, "Generic", "BASS", "DAM", null, null)
                ),
                List.of(),
                List.of(),
                new PlannerContextResponse.PlannerContextDataQuality(3, 1, 1, "high", List.of())
        );
        String rawResponse = """
                {
                  "summary": "Plano com historico.",
                  "lureRanking": [
                    {"rank": 1, "lure": "Vinil verde natural", "reason": "Bom alinhamento."}
                  ],
                  "planA": "Comecar com Vinil verde natural.",
                  "planB": "Cobrir agua com Crankbait pequeno natural.",
                  "planC": "Testar Popper branco em sombra.",
                  "avoid": [],
                  "confidence": "high",
                  "warnings": []
                }
                """;
        AiRecommendation previousRecommendation = new AiRecommendation(
                plan,
                null,
                "PLAN",
                1,
                "{}",
                "{}",
                "Previous",
                "[]",
                "A",
                "B",
                "C",
                "[]",
                "medium",
                "[]"
        );
        List<RecommendationExecution> executions = List.of(
                new RecommendationExecution(previousRecommendation, plan, null, "PLAN", 1, "PLAN_A", true, "CATCH", true, 5, null, null, null),
                new RecommendationExecution(previousRecommendation, plan, null, "PLAN", 1, "PLAN_B", true, "BITE", true, 4, null, null, null),
                new RecommendationExecution(previousRecommendation, plan, null, "PLAN", 1, "PLAN_C", true, "NO_RESULT", false, 3, null, null, null)
        );

        when(fishingPlanRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(1L)).thenReturn(Optional.of(new com.lurepilot.backend.model.WeatherSnapshot()));
        when(plannerContextService.buildContext(1L)).thenReturn(context);
        when(lmStudioClient.createChatCompletion(anyString(), anyString())).thenReturn(rawResponse);
        when(recommendationExecutionRepository.findRecentByPlanAndType(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq("PLAN"), org.mockito.ArgumentMatchers.any(Pageable.class))).thenReturn(executions);
        when(aiRecommendationRepository.countByPlanIdAndRecommendationType(1L, "PLAN")).thenReturn(1L);
        when(aiRecommendationRepository.save(any(AiRecommendation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AiPlanRecommendationResponse response = aiRecommendationService.createPlanRecommendation(new CreateAiPlanRecommendationRequest(1L));

        assertThat(response.confidenceScore()).isGreaterThanOrEqualTo(75);
        assertThat(response.confidenceReason()).contains("Historico de execucao");
        assertThat(response.confidenceReason()).contains("67%");
    }

    @Test
    void createPlanRecommendationReturnsBadGatewayWhenLmStudioIsUnavailable() {
        FishingSpot spot = new FishingSpot("Barragem Norte", null, 38.7, -9.1, "DAM", "BASS");
        ReflectionTestUtils.setField(spot, "id", 10L);
        FishingPlan plan = new FishingPlan(spot, LocalDate.of(2026, 7, 24), LocalTime.of(19, 0), "BASS", "CLEAR", "LOW", null);
        ReflectionTestUtils.setField(plan, "id", 1L);
        PlannerContextResponse context = new PlannerContextResponse(
                new PlannerContextResponse.PlannerContextPlan(1L, LocalDate.of(2026, 7, 24), LocalTime.of(19, 0), "BASS", "CLEAR", "LOW", null),
                new PlannerContextResponse.PlannerContextSpot(10L, "Barragem Norte", null, 38.7, -9.1, "DAM", "BASS"),
                null,
                List.of(new PlannerContextResponse.PlannerContextLure(100L, "Vinil verde natural", "SOFT_BAIT", "GREEN", "10cm", 7.0, "Generic", "BASS", "DAM", null, null)),
                List.of(),
                List.of(),
                new PlannerContextResponse.PlannerContextDataQuality(1, 0, 0, "medium", List.of())
        );

        when(fishingPlanRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(1L)).thenReturn(Optional.empty());
        when(plannerContextService.buildContext(1L)).thenReturn(context);
        when(lmStudioClient.createChatCompletion(anyString(), anyString()))
                .thenThrow(new IllegalStateException("Connection refused"));

        Throwable thrown = org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                aiRecommendationService.createPlanRecommendation(new CreateAiPlanRecommendationRequest(1L))
        ).isInstanceOf(ResponseStatusException.class).actual();
        ResponseStatusException exception = (ResponseStatusException) thrown;

        assertThat(exception.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.BAD_GATEWAY);
    }
}
