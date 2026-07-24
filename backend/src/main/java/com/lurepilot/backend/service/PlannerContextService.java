package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.PlannerContextResponse;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingPlanLure;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.model.WeatherSnapshot;
import com.lurepilot.backend.repository.FishingPlanLureRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PlannerContextService {

    private final FishingPlanRepository fishingPlanRepository;
    private final FishingPlanLureRepository fishingPlanLureRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final WeatherSnapshotRepository weatherSnapshotRepository;

    public PlannerContextService(
            FishingPlanRepository fishingPlanRepository,
            FishingPlanLureRepository fishingPlanLureRepository,
            FishingSessionRepository fishingSessionRepository,
            WeatherSnapshotRepository weatherSnapshotRepository
    ) {
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingPlanLureRepository = fishingPlanLureRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.weatherSnapshotRepository = weatherSnapshotRepository;
    }

    public PlannerContextResponse buildContext(Long planId) {
        FishingPlan plan = fishingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));
        FishingSpot spot = plan.getSpot();

        List<PlannerContextResponse.PlannerContextLure> selectedLures = fishingPlanLureRepository.findByPlanIdOrderByIdAsc(planId)
                .stream()
                .map(this::toLureContext)
                .toList();

        List<PlannerContextResponse.PlannerContextSession> recentSpotSessions = fishingSessionRepository.findTop5BySpotIdOrderByDateDescIdDesc(spot.getId())
                .stream()
                .map(this::toSessionContext)
                .toList();

        List<PlannerContextResponse.PlannerContextSession> recentSpeciesSessions = fishingSessionRepository.findTop5ByTargetSpeciesIgnoreCaseOrderByDateDescIdDesc(plan.getTargetSpecies())
                .stream()
                .map(this::toSessionContext)
                .toList();

        PlannerContextResponse.PlannerContextWeather weather = weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(planId)
                .map(this::toWeatherContext)
                .orElse(null);

        return new PlannerContextResponse(
                toPlanContext(plan),
                toSpotContext(spot),
                weather,
                selectedLures,
                recentSpotSessions,
                recentSpeciesSessions,
                buildDataQuality(weather, selectedLures, recentSpotSessions, recentSpeciesSessions)
        );
    }

    private PlannerContextResponse.PlannerContextPlan toPlanContext(FishingPlan plan) {
        return new PlannerContextResponse.PlannerContextPlan(
                plan.getId(),
                plan.getPlannedDate(),
                plan.getPlannedTime(),
                plan.getTargetSpecies(),
                plan.getWaterClarity(),
                plan.getWaterLevel(),
                plan.getNotes()
        );
    }

    private PlannerContextResponse.PlannerContextSpot toSpotContext(FishingSpot spot) {
        return new PlannerContextResponse.PlannerContextSpot(
                spot.getId(),
                spot.getName(),
                spot.getDescription(),
                spot.getLatitude(),
                spot.getLongitude(),
                spot.getWaterType(),
                spot.getFavoriteSpecies()
        );
    }

    private PlannerContextResponse.PlannerContextLure toLureContext(FishingPlanLure fishingPlanLure) {
        Lure lure = fishingPlanLure.getLure();
        LureLibraryItem libraryItem = lure.getLibraryItem();

        return new PlannerContextResponse.PlannerContextLure(
                lure.getId(),
                lure.getName(),
                lure.getType(),
                lure.getColor(),
                lure.getSize(),
                lure.getWeight(),
                lure.getBrand(),
                lure.getTargetSpecies(),
                lure.getWaterType(),
                libraryItem == null ? null : libraryItem.getId(),
                libraryItem == null ? null : libraryItem.getName()
        );
    }

    private PlannerContextResponse.PlannerContextWeather toWeatherContext(WeatherSnapshot weatherSnapshot) {
        return new PlannerContextResponse.PlannerContextWeather(
                weatherSnapshot.getId(),
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

    private PlannerContextResponse.PlannerContextSession toSessionContext(FishingSession session) {
        FishingSpot spot = session.getSpot();

        return new PlannerContextResponse.PlannerContextSession(
                session.getId(),
                spot.getId(),
                spot.getName(),
                session.getDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getTargetSpecies(),
                session.getWaterClarity(),
                session.getWaterLevel(),
                statusOrDefault(session).name().toLowerCase(Locale.ROOT),
                session.getSuccess(),
                session.getDurationMinutes(),
                session.getResultSummary(),
                session.getRating(),
                session.getNotes()
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

    private PlannerContextResponse.PlannerContextDataQuality buildDataQuality(
            PlannerContextResponse.PlannerContextWeather weather,
            List<PlannerContextResponse.PlannerContextLure> selectedLures,
            List<PlannerContextResponse.PlannerContextSession> recentSpotSessions,
            List<PlannerContextResponse.PlannerContextSession> recentSpeciesSessions
    ) {
        List<String> warnings = new ArrayList<>();

        if (selectedLures.isEmpty()) {
            warnings.add("No lures selected for this fishing plan yet.");
        }

        if (weather == null) {
            warnings.add("No weather snapshot found for this fishing plan.");
        }

        if (recentSpotSessions.isEmpty()) {
            warnings.add("No recent history found for this spot.");
        }

        if (recentSpeciesSessions.isEmpty()) {
            warnings.add("No recent history found for this target species.");
        }

        String confidenceHint = warnings.isEmpty() ? "medium" : "low";

        return new PlannerContextResponse.PlannerContextDataQuality(
                selectedLures.size(),
                recentSpotSessions.size(),
                recentSpeciesSessions.size(),
                confidenceHint,
                warnings
        );
    }
}
