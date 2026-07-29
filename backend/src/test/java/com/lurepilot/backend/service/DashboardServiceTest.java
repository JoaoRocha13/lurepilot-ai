package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.DashboardResponse;
import com.lurepilot.backend.model.AiRecommendation;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.WeatherSnapshot;
import com.lurepilot.backend.repository.AiRecommendationRepository;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.FishSpeciesRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.FishingSpotRepository;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import com.lurepilot.backend.repository.LureRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private FishingSpotRepository fishingSpotRepository;

    @Mock
    private FishSpeciesRepository fishSpeciesRepository;

    @Mock
    private LureRepository lureRepository;

    @Mock
    private LureLibraryItemRepository lureLibraryItemRepository;

    @Mock
    private FishingPlanRepository fishingPlanRepository;

    @Mock
    private FishingSessionRepository fishingSessionRepository;

    @Mock
    private CatchRepository catchRepository;

    @Mock
    private SessionLureRepository sessionLureRepository;

    @Mock
    private WeatherSnapshotRepository weatherSnapshotRepository;

    @Mock
    private AiRecommendationRepository aiRecommendationRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(
                fishingSpotRepository,
                fishSpeciesRepository,
                lureRepository,
                lureLibraryItemRepository,
                fishingPlanRepository,
                fishingSessionRepository,
                catchRepository,
                sessionLureRepository,
                weatherSnapshotRepository,
                aiRecommendationRepository
        );
    }

    @Test
    void getDashboardReturnsActionableSummaryBlocks() {
        LocalDate today = LocalDate.now();
        FishingSpot spot = new FishingSpot("Barragem Norte", null, 38.7, -9.1, "Freshwater", "Black Bass");
        ReflectionTestUtils.setField(spot, "id", 1L);

        FishingSession nextSession = new FishingSession(
                spot,
                null,
                today.plusDays(1),
                LocalTime.of(7, 0),
                null,
                "Black Bass",
                "Clear",
                "Normal",
                "Sessao planeada cedo.",
                null
        );
        nextSession.setStatus(FishingSessionStatus.PLANNED);
        ReflectionTestUtils.setField(nextSession, "id", 10L);

        FishingPlan laterPlan = new FishingPlan(
                spot,
                today.plusDays(2),
                LocalTime.of(18, 30),
                "Black Bass",
                "Clear",
                "Normal",
                "Plano de fim de tarde."
        );
        ReflectionTestUtils.setField(laterPlan, "id", 20L);

        FishingSession finishedSession = new FishingSession(
                spot,
                laterPlan,
                today.minusDays(1),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0),
                "Black Bass",
                "Clear",
                "Normal",
                "Sessao terminada.",
                true
        );
        finishedSession.setStatus(FishingSessionStatus.FINISHED);
        finishedSession.setResultSummary("Uma captura com vinil.");
        finishedSession.setRating(4);
        ReflectionTestUtils.setField(finishedSession, "id", 30L);

        WeatherSnapshot weatherSnapshot = new WeatherSnapshot(
                null,
                nextSession,
                "IPMA",
                1110600,
                "Lisboa",
                38.766,
                -9.1286,
                today.plusDays(1),
                null,
                2,
                18.0,
                27.0,
                0.0,
                "NW",
                2,
                null
        );
        ReflectionTestUtils.setField(weatherSnapshot, "id", 40L);

        AiRecommendation pendingRecommendation = new AiRecommendation(
                laterPlan,
                null,
                "PLAN",
                1,
                "{}",
                "{}",
                "Plano pendente de avaliacao.",
                "[]",
                "A",
                "B",
                "C",
                "[]",
                "medium",
                "[]"
        );
        pendingRecommendation.setConfidenceScore(68);
        ReflectionTestUtils.setField(pendingRecommendation, "id", 50L);

        when(fishingSpotRepository.count()).thenReturn(1L);
        when(fishSpeciesRepository.count()).thenReturn(1L);
        when(lureRepository.count()).thenReturn(3L);
        when(lureLibraryItemRepository.count()).thenReturn(2L);
        when(fishingPlanRepository.count()).thenReturn(1L);
        when(fishingSessionRepository.count()).thenReturn(4L);
        when(fishingSessionRepository.countByStatus(FishingSessionStatus.FINISHED)).thenReturn(2L);
        when(fishingSessionRepository.countBySuccessTrue()).thenReturn(1L);
        when(catchRepository.count()).thenReturn(2L);
        when(catchRepository.sumTotalQuantity()).thenReturn(3L);
        when(catchRepository.sumQuantityBySessionId(30L)).thenReturn(2L);
        when(fishingSessionRepository.findFirstByStatusAndDateGreaterThanEqualOrderByDateAscStartTimeAscIdAsc(FishingSessionStatus.PLANNED, today)).thenReturn(Optional.of(nextSession));
        when(fishingPlanRepository.findFirstByPlannedDateGreaterThanEqualOrderByPlannedDateAscPlannedTimeAscIdAsc(today)).thenReturn(Optional.of(laterPlan));
        when(fishingPlanRepository.findTop5ByPlannedDateGreaterThanEqualOrderByPlannedDateAscPlannedTimeAscIdAsc(today)).thenReturn(List.of(laterPlan));
        when(fishingSessionRepository.findTop5ByStatusOrderByDateDescStartTimeDescIdDesc(FishingSessionStatus.ACTIVE)).thenReturn(List.of());
        when(fishingSessionRepository.findTop5ByStatusOrderByDateDescStartTimeDescIdDesc(FishingSessionStatus.FINISHED)).thenReturn(List.of(finishedSession));
        when(fishingSessionRepository.findTop5ByOrderByDateDescIdDesc()).thenReturn(List.of(finishedSession));
        when(catchRepository.findTop5ByOrderByIdDesc()).thenReturn(List.of());
        when(weatherSnapshotRepository.findFirstBySessionIdOrderByCapturedAtDescIdDesc(10L)).thenReturn(Optional.of(weatherSnapshot));
        when(weatherSnapshotRepository.findTop5ByOrderByCapturedAtDescIdDesc()).thenReturn(List.of(weatherSnapshot));
        Object[] bestLureRow = new Object[]{
                70L,
                "Vinil verde natural",
                "Soft bait",
                4L,
                3L,
                today.minusDays(1)
        };
        when(sessionLureRepository.findBestRecentLures(any(LocalDate.class), any(Pageable.class))).thenReturn(List.<Object[]>of(bestLureRow));
        when(aiRecommendationRepository.countPendingEvaluation()).thenReturn(1L);
        when(aiRecommendationRepository.findPendingEvaluation(any(Pageable.class))).thenReturn(List.of(pendingRecommendation));

        DashboardResponse response = dashboardService.getDashboard();

        assertThat(response.nextPlannedSession().sourceType()).isEqualTo("session");
        assertThat(response.nextPlannedSession().sessionId()).isEqualTo(10L);
        assertThat(response.successRate()).isEqualTo(50.0);
        assertThat(response.bestRecentLure().lureName()).isEqualTo("Vinil verde natural");
        assertThat(response.bestRecentLure().successRate()).isEqualTo(75.0);
        assertThat(response.relevantWeatherSnapshot().sessionId()).isEqualTo(10L);
        assertThat(response.recentResults()).hasSize(1);
        assertThat(response.recentResults().getFirst().totalFishCaught()).isEqualTo(2L);
        assertThat(response.pendingRecommendationEvaluations()).isEqualTo(1L);
        assertThat(response.pendingRecommendations().getFirst().id()).isEqualTo(50L);
    }
}
