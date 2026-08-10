package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.DashboardActiveSessionResponse;
import com.lurepilot.backend.dto.DashboardBestLureResponse;
import com.lurepilot.backend.dto.DashboardNextPlannedSessionResponse;
import com.lurepilot.backend.dto.DashboardPendingRecommendationResponse;
import com.lurepilot.backend.dto.DashboardRecentSessionResponse;
import com.lurepilot.backend.dto.DashboardRecentCatchResponse;
import com.lurepilot.backend.dto.DashboardRecentResultResponse;
import com.lurepilot.backend.dto.DashboardResponse;
import com.lurepilot.backend.dto.DashboardUpcomingPlanResponse;
import com.lurepilot.backend.dto.DashboardWeatherSnapshotResponse;
import com.lurepilot.backend.model.AiRecommendation;
import com.lurepilot.backend.model.Catch;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.WeatherSnapshot;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.AiRecommendationRepository;
import com.lurepilot.backend.repository.FishSpeciesRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.FishingSpotRepository;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import com.lurepilot.backend.repository.LureRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class DashboardService {

    private final FishingSpotRepository fishingSpotRepository;
    private final FishSpeciesRepository fishSpeciesRepository;
    private final LureRepository lureRepository;
    private final LureLibraryItemRepository lureLibraryItemRepository;
    private final FishingPlanRepository fishingPlanRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final CatchRepository catchRepository;
    private final SessionLureRepository sessionLureRepository;
    private final WeatherSnapshotRepository weatherSnapshotRepository;
    private final AiRecommendationRepository aiRecommendationRepository;

    public DashboardService(
            FishingSpotRepository fishingSpotRepository,
            FishSpeciesRepository fishSpeciesRepository,
            LureRepository lureRepository,
            LureLibraryItemRepository lureLibraryItemRepository,
            FishingPlanRepository fishingPlanRepository,
            FishingSessionRepository fishingSessionRepository,
            CatchRepository catchRepository,
            SessionLureRepository sessionLureRepository,
            WeatherSnapshotRepository weatherSnapshotRepository,
            AiRecommendationRepository aiRecommendationRepository
    ) {
        this.fishingSpotRepository = fishingSpotRepository;
        this.fishSpeciesRepository = fishSpeciesRepository;
        this.lureRepository = lureRepository;
        this.lureLibraryItemRepository = lureLibraryItemRepository;
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.catchRepository = catchRepository;
        this.sessionLureRepository = sessionLureRepository;
        this.weatherSnapshotRepository = weatherSnapshotRepository;
        this.aiRecommendationRepository = aiRecommendationRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        long totalSessions = fishingSessionRepository.count();
        long finishedSessions = fishingSessionRepository.countByStatus(FishingSessionStatus.FINISHED);
        long successfulSessions = fishingSessionRepository.countByStatusAndSuccessTrue(FishingSessionStatus.FINISHED);
        DashboardNextPlannedSessionResponse nextPlannedSession = findNextPlannedSession();
        DashboardBestLureResponse bestRecentLure = findBestRecentLure();
        DashboardWeatherSnapshotResponse relevantWeatherSnapshot = findRelevantWeatherSnapshot(nextPlannedSession)
                .orElse(null);

        List<DashboardUpcomingPlanResponse> upcomingPlans = fishingPlanRepository.findTop5ByPlannedDateGreaterThanEqualOrderByPlannedDateAscPlannedTimeAscIdAsc(LocalDate.now())
                .stream()
                .map(this::toUpcomingPlanResponse)
                .toList();

        List<DashboardActiveSessionResponse> activeSessions = fishingSessionRepository.findTop5ByStatusOrderByDateDescStartTimeDescIdDesc(FishingSessionStatus.ACTIVE)
                .stream()
                .map(this::toActiveSessionResponse)
                .toList();

        List<DashboardRecentResultResponse> recentResults = fishingSessionRepository.findTop5ByStatusOrderByDateDescStartTimeDescIdDesc(FishingSessionStatus.FINISHED)
                .stream()
                .map(this::toRecentResultResponse)
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

        List<DashboardPendingRecommendationResponse> pendingRecommendations = aiRecommendationRepository.findPendingEvaluation(PageRequest.of(0, 5))
                .stream()
                .map(this::toPendingRecommendationResponse)
                .toList();

        return new DashboardResponse(
                fishingSpotRepository.count(),
                fishSpeciesRepository.count(),
                lureRepository.count(),
                lureLibraryItemRepository.count(),
                fishingPlanRepository.count(),
                totalSessions,
                finishedSessions,
                successfulSessions,
                rate(successfulSessions, finishedSessions),
                catchRepository.count(),
                catchRepository.sumTotalQuantity(),
                nextPlannedSession,
                bestRecentLure,
                relevantWeatherSnapshot,
                aiRecommendationRepository.countPendingEvaluation(),
                upcomingPlans,
                activeSessions,
                recentResults,
                recentSessions,
                recentCatches,
                pendingRecommendations,
                recentWeatherSnapshots
        );
    }

    private DashboardNextPlannedSessionResponse findNextPlannedSession() {
        Optional<FishingSession> nextSession = fishingSessionRepository.findFirstByStatusAndDateGreaterThanEqualOrderByDateAscStartTimeAscIdAsc(
                FishingSessionStatus.PLANNED,
                LocalDate.now()
        );
        Optional<FishingPlan> nextPlan = fishingPlanRepository.findFirstByPlannedDateGreaterThanEqualOrderByPlannedDateAscPlannedTimeAscIdAsc(LocalDate.now());

        if (nextSession.isPresent() && nextPlan.isPresent()) {
            FishingSession session = nextSession.get();
            FishingPlan plan = nextPlan.get();
            if (isBeforeOrSame(session.getDate(), session.getStartTime(), plan.getPlannedDate(), plan.getPlannedTime())) {
                return toNextPlannedSessionResponse(session);
            }

            return toNextPlannedSessionResponse(plan);
        }

        if (nextSession.isPresent()) {
            return toNextPlannedSessionResponse(nextSession.get());
        }

        return nextPlan.map(this::toNextPlannedSessionResponse).orElse(null);
    }

    private DashboardBestLureResponse findBestRecentLure() {
        LocalDate dateFrom = LocalDate.now().minusDays(60);
        return sessionLureRepository.findBestRecentLures(dateFrom, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(this::toBestLureResponse)
                .orElse(null);
    }

    private Optional<DashboardWeatherSnapshotResponse> findRelevantWeatherSnapshot(DashboardNextPlannedSessionResponse nextPlannedSession) {
        Optional<WeatherSnapshot> weatherSnapshot = Optional.empty();

        if (nextPlannedSession != null && nextPlannedSession.sessionId() != null) {
            weatherSnapshot = weatherSnapshotRepository.findFirstBySessionIdOrderByCapturedAtDescIdDesc(nextPlannedSession.sessionId());
        }

        if (weatherSnapshot.isEmpty() && nextPlannedSession != null && nextPlannedSession.planId() != null) {
            weatherSnapshot = weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(nextPlannedSession.planId());
        }

        return weatherSnapshot
                .or(weatherSnapshotRepository::findFirstByOrderByCapturedAtDescIdDesc)
                .map(this::toWeatherSnapshotResponse);
    }

    private DashboardNextPlannedSessionResponse toNextPlannedSessionResponse(FishingSession session) {
        FishingSpot spot = session.getSpot();

        return new DashboardNextPlannedSessionResponse(
                "session",
                session.getId(),
                session.getPlan() == null ? null : session.getPlan().getId(),
                spot.getId(),
                spot.getName(),
                session.getDate(),
                session.getStartTime(),
                session.getTargetSpecies(),
                session.getWaterClarity(),
                session.getWaterLevel(),
                session.getNotes()
        );
    }

    private DashboardNextPlannedSessionResponse toNextPlannedSessionResponse(FishingPlan plan) {
        FishingSpot spot = plan.getSpot();

        return new DashboardNextPlannedSessionResponse(
                "plan",
                null,
                plan.getId(),
                spot.getId(),
                spot.getName(),
                plan.getPlannedDate(),
                plan.getPlannedTime(),
                plan.getTargetSpecies(),
                plan.getWaterClarity(),
                plan.getWaterLevel(),
                plan.getNotes()
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

    private DashboardRecentResultResponse toRecentResultResponse(FishingSession session) {
        FishingSpot spot = session.getSpot();

        return new DashboardRecentResultResponse(
                session.getId(),
                spot.getId(),
                spot.getName(),
                session.getDate(),
                session.getStartTime(),
                session.getTargetSpecies(),
                session.getSuccess(),
                catchRepository.sumQuantityBySessionId(session.getId()),
                session.getResultSummary(),
                session.getRating()
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
                catchRecord.getReleased(),
                catchRecord.getPhotoUrl(),
                catchRecord.getPhotoThumbnailUrl()
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
                weatherSnapshot.getCurrentTemperature(),
                weatherSnapshot.getRelativeHumidity(),
                weatherSnapshot.getWindSpeedKmh(),
                weatherSnapshot.getWindGustsKmh(),
                weatherSnapshot.getPressureMsl(),
                weatherSnapshot.getCapturedAt()
        );
    }

    private DashboardBestLureResponse toBestLureResponse(Object[] row) {
        long uses = toLong(row[3]);
        long successfulSessions = toLong(row[4]);

        return new DashboardBestLureResponse(
                (Long) row[0],
                (String) row[1],
                (String) row[2],
                uses,
                successfulSessions,
                rate(successfulSessions, uses),
                (LocalDate) row[5]
        );
    }

    private DashboardPendingRecommendationResponse toPendingRecommendationResponse(AiRecommendation recommendation) {
        return new DashboardPendingRecommendationResponse(
                recommendation.getId(),
                recommendation.getPlan() == null ? null : recommendation.getPlan().getId(),
                recommendation.getSession() == null ? null : recommendation.getSession().getId(),
                recommendation.getRecommendationType(),
                recommendation.getVersion(),
                recommendation.getSummary(),
                recommendation.getConfidence(),
                recommendation.getConfidenceScore(),
                recommendation.getCreatedAt()
        );
    }

    private boolean isBeforeOrSame(LocalDate firstDate, java.time.LocalTime firstTime, LocalDate secondDate, java.time.LocalTime secondTime) {
        int dateCompare = firstDate.compareTo(secondDate);
        if (dateCompare != 0) {
            return dateCompare < 0;
        }

        java.time.LocalTime normalizedFirstTime = firstTime == null ? java.time.LocalTime.MIN : firstTime;
        java.time.LocalTime normalizedSecondTime = secondTime == null ? java.time.LocalTime.MIN : secondTime;
        return !normalizedFirstTime.isAfter(normalizedSecondTime);
    }

    private double rate(long value, long total) {
        if (total == 0) {
            return 0.0;
        }

        return Math.round((double) value * 1000.0 / total) / 10.0;
    }

    private long toLong(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
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
