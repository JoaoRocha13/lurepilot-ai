package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.DashboardActiveSessionResponse;
import com.lurepilot.backend.dto.DashboardRecentSessionResponse;
import com.lurepilot.backend.dto.DashboardRecentCatchResponse;
import com.lurepilot.backend.dto.DashboardResponse;
import com.lurepilot.backend.dto.DashboardUpcomingPlanResponse;
import com.lurepilot.backend.dto.DashboardWeatherSnapshotResponse;
import com.lurepilot.backend.model.Catch;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.WeatherSnapshot;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.FishSpeciesRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.FishingSpotRepository;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import com.lurepilot.backend.repository.LureRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardService {

    private final FishingSpotRepository fishingSpotRepository;
    private final FishSpeciesRepository fishSpeciesRepository;
    private final LureRepository lureRepository;
    private final LureLibraryItemRepository lureLibraryItemRepository;
    private final FishingPlanRepository fishingPlanRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final CatchRepository catchRepository;
    private final WeatherSnapshotRepository weatherSnapshotRepository;

    public DashboardService(
            FishingSpotRepository fishingSpotRepository,
            FishSpeciesRepository fishSpeciesRepository,
            LureRepository lureRepository,
            LureLibraryItemRepository lureLibraryItemRepository,
            FishingPlanRepository fishingPlanRepository,
            FishingSessionRepository fishingSessionRepository,
            CatchRepository catchRepository,
            WeatherSnapshotRepository weatherSnapshotRepository
    ) {
        this.fishingSpotRepository = fishingSpotRepository;
        this.fishSpeciesRepository = fishSpeciesRepository;
        this.lureRepository = lureRepository;
        this.lureLibraryItemRepository = lureLibraryItemRepository;
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.catchRepository = catchRepository;
        this.weatherSnapshotRepository = weatherSnapshotRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        List<DashboardUpcomingPlanResponse> upcomingPlans = fishingPlanRepository.findTop5ByPlannedDateGreaterThanEqualOrderByPlannedDateAscPlannedTimeAscIdAsc(LocalDate.now())
                .stream()
                .map(this::toUpcomingPlanResponse)
                .toList();

        List<DashboardActiveSessionResponse> activeSessions = fishingSessionRepository.findTop5ByStatusOrderByDateDescStartTimeDescIdDesc(FishingSessionStatus.ACTIVE)
                .stream()
                .map(this::toActiveSessionResponse)
                .toList();

        List<DashboardRecentSessionResponse> recentSessions = fishingSessionRepository.findTop5ByOrderByDateDescIdDesc()
                .stream()
                .map(this::toRecentSessionResponse)
                .toList();

        List<DashboardRecentCatchResponse> recentCatches = catchRepository.findTop5ByOrderByIdDesc()
                .stream()
                .map(this::toRecentCatchResponse)
                .toList();

        List<DashboardWeatherSnapshotResponse> recentWeatherSnapshots = weatherSnapshotRepository.findTop5ByOrderByCapturedAtDescIdDesc()
                .stream()
                .map(this::toWeatherSnapshotResponse)
                .toList();

        return new DashboardResponse(
                fishingSpotRepository.count(),
                fishSpeciesRepository.count(),
                lureRepository.count(),
                lureLibraryItemRepository.count(),
                fishingPlanRepository.count(),
                fishingSessionRepository.count(),
                fishingSessionRepository.countBySuccessTrue(),
                catchRepository.count(),
                catchRepository.sumTotalQuantity(),
                upcomingPlans,
                activeSessions,
                recentSessions,
                recentCatches,
                recentWeatherSnapshots
        );
    }

    private DashboardUpcomingPlanResponse toUpcomingPlanResponse(FishingPlan plan) {
        FishingSpot spot = plan.getSpot();

        return new DashboardUpcomingPlanResponse(
                plan.getId(),
                spot.getId(),
                spot.getName(),
                plan.getPlannedDate(),
                plan.getPlannedTime(),
                plan.getTargetSpecies(),
                plan.getWaterClarity(),
                plan.getWaterLevel()
        );
    }

    private DashboardActiveSessionResponse toActiveSessionResponse(FishingSession session) {
        FishingSpot spot = session.getSpot();

        return new DashboardActiveSessionResponse(
                session.getId(),
                spot.getId(),
                spot.getName(),
                session.getPlan() == null ? null : session.getPlan().getId(),
                session.getDate(),
                session.getStartTime(),
                session.getTargetSpecies(),
                session.getNotes()
        );
    }

    private DashboardRecentSessionResponse toRecentSessionResponse(FishingSession session) {
        FishingSpot spot = session.getSpot();

        return new DashboardRecentSessionResponse(
                session.getId(),
                spot.getId(),
                spot.getName(),
                session.getDate(),
                session.getStartTime(),
                statusOrDefault(session).name().toLowerCase(Locale.ROOT),
                session.getTargetSpecies(),
                session.getSuccess()
        );
    }

    private DashboardRecentCatchResponse toRecentCatchResponse(Catch catchRecord) {
        FishingSession session = catchRecord.getSession();
        FishingSpot spot = session.getSpot();

        return new DashboardRecentCatchResponse(
                catchRecord.getId(),
                session.getId(),
                spot.getId(),
                spot.getName(),
                catchRecord.getSpecies(),
                catchRecord.getQuantity(),
                catchRecord.getSizeCm(),
                catchRecord.getWeightKg(),
                catchRecord.getReleased()
        );
    }

    private DashboardWeatherSnapshotResponse toWeatherSnapshotResponse(WeatherSnapshot weatherSnapshot) {
        return new DashboardWeatherSnapshotResponse(
                weatherSnapshot.getId(),
                weatherSnapshot.getPlan() == null ? null : weatherSnapshot.getPlan().getId(),
                weatherSnapshot.getSession() == null ? null : weatherSnapshot.getSession().getId(),
                weatherSnapshot.getSourceLocationName(),
                weatherSnapshot.getForecastDate(),
                weatherSnapshot.getTemperatureMin(),
                weatherSnapshot.getTemperatureMax(),
                weatherSnapshot.getPrecipitationProbability(),
                weatherSnapshot.getWindDirection(),
                weatherSnapshot.getWindSpeedClass(),
                weatherSnapshot.getCapturedAt()
        );
    }

    private FishingSessionStatus statusOrDefault(FishingSession session) {
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
}
