package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.InsightRecommendationPerformanceResponse;
import com.lurepilot.backend.dto.InsightTopLureResponse;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.RecommendationExecutionRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsightsServiceTest {

    @Mock
    private SessionLureRepository sessionLureRepository;

    @Mock
    private FishingSessionRepository fishingSessionRepository;

    @Mock
    private RecommendationExecutionRepository recommendationExecutionRepository;

    private InsightsService insightsService;

    @BeforeEach
    void setUp() {
        insightsService = new InsightsService(
                sessionLureRepository,
                fishingSessionRepository,
                recommendationExecutionRepository
        );
    }

    @Test
    void getTopLuresReturnsPracticalRatesAndClampsLimit() {
        LocalDate dateFrom = LocalDate.of(2026, 7, 1);
        LocalDate dateTo = LocalDate.of(2026, 7, 31);
        when(sessionLureRepository.findTopLureInsights(
                eq(dateFrom),
                eq(dateTo),
                eq("Black Bass"),
                eq(1L),
                eq(2L),
                any(Pageable.class)
        )).thenReturn(List.<Object[]>of(new Object[]{
                2L,
                "Vinil verde natural",
                "SOFT_BAIT",
                4L,
                3L,
                5L,
                LocalDate.of(2026, 7, 24)
        }));

        List<InsightTopLureResponse> response = insightsService.getTopLures(
                dateFrom,
                dateTo,
                "Black Bass",
                1L,
                2L,
                100
        );

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().lureName()).isEqualTo("Vinil verde natural");
        assertThat(response.getFirst().successRate()).isEqualTo(75.0);
        assertThat(response.getFirst().totalFishCaught()).isEqualTo(5L);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(sessionLureRepository).findTopLureInsights(eq(dateFrom), eq(dateTo), eq("Black Bass"), eq(1L), eq(2L), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
    }

    @Test
    void getRecommendationPerformanceNormalizesFiltersAndDateRange() {
        LocalDate dateFrom = LocalDate.of(2026, 7, 1);
        LocalDate dateTo = LocalDate.of(2026, 7, 31);
        Instant lastExecutionAt = Instant.parse("2026-07-24T14:00:00Z");
        when(recommendationExecutionRepository.findRecommendationPerformanceInsights(
                eq(Instant.parse("2026-07-01T00:00:00Z")),
                eq(Instant.parse("2026-08-01T00:00:00Z")),
                eq("Black Bass"),
                eq(1L),
                eq(2L),
                eq("PLAN"),
                any(Pageable.class)
        )).thenReturn(List.<Object[]>of(new Object[]{
                "PLAN",
                "PLAN_A",
                4L,
                3L,
                2L,
                4.5,
                lastExecutionAt
        }));

        List<InsightRecommendationPerformanceResponse> response = insightsService.getRecommendationPerformance(
                dateFrom,
                dateTo,
                " Black Bass ",
                1L,
                2L,
                "plan",
                null
        );

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().recommendationType()).isEqualTo("PLAN");
        assertThat(response.getFirst().recommendationStep()).isEqualTo("PLAN_A");
        assertThat(response.getFirst().followRate()).isEqualTo(75.0);
        assertThat(response.getFirst().successRate()).isEqualTo(50.0);
        assertThat(response.getFirst().averageRating()).isEqualTo(4.5);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(recommendationExecutionRepository).findRecommendationPerformanceInsights(
                eq(Instant.parse("2026-07-01T00:00:00Z")),
                eq(Instant.parse("2026-08-01T00:00:00Z")),
                eq("Black Bass"),
                eq(1L),
                eq(2L),
                eq("PLAN"),
                pageableCaptor.capture()
        );
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
    }
}
