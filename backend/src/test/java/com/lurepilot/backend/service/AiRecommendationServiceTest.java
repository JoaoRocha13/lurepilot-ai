package com.lurepilot.backend.service;

import com.lurepilot.backend.client.LmStudioClient;
import com.lurepilot.backend.dto.AiPlanRecommendationResponse;
import com.lurepilot.backend.dto.CreateAiPlanRecommendationRequest;
import com.lurepilot.backend.dto.PlannerContextResponse;
import com.lurepilot.backend.model.AiRecommendation;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.AiRecommendationRepository;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.FishingPlanLureRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.SessionEventRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
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
                  "planA": "Comecar com vinil.",
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
        assertThat(response.warnings()).anySatisfy(warning -> assertThat(warning).contains("Spinnerbait inventado"));
        verify(weatherSnapshotService).createIpmaSnapshotForPlan(1L);
    }
}
