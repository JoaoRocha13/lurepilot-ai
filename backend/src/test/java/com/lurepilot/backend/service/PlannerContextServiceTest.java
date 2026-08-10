package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.SolunarForecastResponse;
import com.lurepilot.backend.dto.SolunarPeriodResponse;
import com.lurepilot.backend.dto.WeatherHourlyResponse;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.WeatherSnapshot;
import com.lurepilot.backend.repository.FishSpeciesRepository;
import com.lurepilot.backend.repository.FishingPlanLureRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlannerContextServiceTest {

    @Mock
    private FishingPlanRepository fishingPlanRepository;

    @Mock
    private FishingPlanLureRepository fishingPlanLureRepository;

    @Mock
    private FishingSessionRepository fishingSessionRepository;

    @Mock
    private WeatherSnapshotRepository weatherSnapshotRepository;

    @Mock
    private SolunarService solunarService;

    @Mock
    private FishSpeciesRepository fishSpeciesRepository;

    @Mock
    private LureLibraryItemRepository lureLibraryItemRepository;

    private PlannerContextService plannerContextService;

    @BeforeEach
    void setUp() {
        plannerContextService = new PlannerContextService(
                fishingPlanRepository,
                fishingPlanLureRepository,
                fishingSessionRepository,
                weatherSnapshotRepository,
                solunarService,
                fishSpeciesRepository,
                lureLibraryItemRepository
        );
    }

    @Test
    void includesSolunarForecastInPlannerContext() {
        LocalDate date = LocalDate.of(2026, 8, 6);
        FishingSpot spot = new FishingSpot("Alqueva", null, 38.2, -7.5, "Freshwater", "Reservoir", "Black Bass");
        ReflectionTestUtils.setField(spot, "id", 10L);
        FishingPlan plan = new FishingPlan(spot, date, LocalTime.of(19, 0), "Black Bass", "CLEAR", "NORMAL", null);
        ReflectionTestUtils.setField(plan, "id", 1L);

        SolunarForecastResponse forecast = new SolunarForecastResponse(
                10L,
                "Alqueva",
                date,
                "Europe/Lisbon",
                "2026-08-06T06:36+01:00",
                "2026-08-06T20:34+01:00",
                null,
                "2026-08-06T14:51+01:00",
                "LAST_QUARTER",
                45.9,
                List.of(new SolunarPeriodResponse(
                        "UPPER_TRANSIT",
                        "Transito lunar superior",
                        "2026-08-06T07:30+01:00",
                        "2026-08-06T06:30+01:00",
                        "2026-08-06T08:30+01:00"
                )),
                List.of(),
                "LOW",
                "Indicador tradicional de atividade solunar."
        );

        when(fishingPlanRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(fishingPlanLureRepository.findByPlanIdOrderByIdAsc(1L)).thenReturn(List.of());
        when(fishingSessionRepository.findTop5BySpotIdOrderByDateDescIdDesc(10L)).thenReturn(List.of());
        when(fishingSessionRepository.findTop5ByTargetSpeciesIgnoreCaseOrderByDateDescIdDesc("Black Bass"))
                .thenReturn(List.of());
        when(weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(1L)).thenReturn(Optional.empty());
        when(fishSpeciesRepository.findAll()).thenReturn(List.of());
        when(solunarService.getForecast(10L, date)).thenReturn(forecast);

        var context = plannerContextService.buildContext(1L);

        assertThat(context.solunar()).isNotNull();
        assertThat(context.solunar().moonPhase()).isEqualTo("LAST_QUARTER");
        assertThat(context.solunar().moonIlluminationPercent()).isEqualTo(45.9);
        assertThat(context.solunar().majorPeriods()).hasSize(1);
        assertThat(context.dataQuality().warnings())
                .doesNotContain("No solunar forecast available for this fishing plan.");
        verify(solunarService).getForecast(10L, date);
    }

    @Test
    void keepsOnlyHourlyWeatherRelevantToThePlannedTime() throws Exception {
        LocalDate date = LocalDate.of(2026, 8, 6);
        FishingSpot spot = new FishingSpot("Alqueva", null, 38.2, -7.5, "Freshwater", "Reservoir", "Black Bass");
        ReflectionTestUtils.setField(spot, "id", 10L);
        FishingPlan plan = new FishingPlan(spot, date, LocalTime.of(9, 0), "Black Bass", "CLEAR", "NORMAL", null);
        ReflectionTestUtils.setField(plan, "id", 1L);

        List<WeatherHourlyResponse> hourly = List.of(
                new WeatherHourlyResponse("2026-08-06T06:00", 18.0, 70.0, 0.0, 0.0, 1, 5.0, 0, 8.0),
                new WeatherHourlyResponse("2026-08-06T07:00", 19.0, 70.0, 0.0, 0.0, 1, 5.0, 0, 8.0),
                new WeatherHourlyResponse("2026-08-06T08:00", 20.0, 70.0, 0.0, 0.0, 1, 5.0, 0, 8.0),
                new WeatherHourlyResponse("2026-08-06T09:00", 21.0, 70.0, 0.0, 0.0, 1, 5.0, 0, 8.0),
                new WeatherHourlyResponse("2026-08-06T10:00", 22.0, 70.0, 0.0, 0.0, 1, 5.0, 0, 8.0),
                new WeatherHourlyResponse("2026-08-06T11:00", 23.0, 70.0, 0.0, 0.0, 1, 5.0, 0, 8.0),
                new WeatherHourlyResponse("2026-08-06T12:00", 24.0, 70.0, 0.0, 0.0, 1, 5.0, 0, 8.0),
                new WeatherHourlyResponse("2026-08-06T13:00", 25.0, 70.0, 0.0, 0.0, 1, 5.0, 0, 8.0)
        );
        WeatherSnapshot snapshot = new WeatherSnapshot(
                plan,
                null,
                "OPEN_METEO",
                null,
                "Alqueva",
                38.2,
                -7.5,
                date,
                Instant.now(),
                1,
                18.0,
                25.0,
                0.0,
                "NW",
                1,
                21.0,
                21.0,
                70.0,
                0.0,
                1015.0,
                10,
                5.0,
                8.0,
                "2026-08-06T06:00",
                "2026-08-06T20:00",
                new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(hourly),
                null
        );

        when(fishingPlanRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(fishingPlanLureRepository.findByPlanIdOrderByIdAsc(1L)).thenReturn(List.of());
        when(fishingSessionRepository.findTop5BySpotIdOrderByDateDescIdDesc(10L)).thenReturn(List.of());
        when(fishingSessionRepository.findTop5ByTargetSpeciesIgnoreCaseOrderByDateDescIdDesc("Black Bass"))
                .thenReturn(List.of());
        when(weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(1L)).thenReturn(Optional.of(snapshot));
        when(fishSpeciesRepository.findAll()).thenReturn(List.of());

        var context = plannerContextService.buildContext(1L);

        assertThat(context.weather().hourlyForecast())
                .extracting(WeatherHourlyResponse::time)
                .containsExactly(
                        "2026-08-06T06:00",
                        "2026-08-06T07:00",
                        "2026-08-06T08:00",
                        "2026-08-06T09:00",
                        "2026-08-06T10:00",
                        "2026-08-06T11:00"
                );
    }
}
