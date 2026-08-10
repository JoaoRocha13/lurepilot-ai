package com.lurepilot.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lurepilot.backend.dto.PlannerContextResponse;
import com.lurepilot.backend.dto.SolunarForecastResponse;
import com.lurepilot.backend.dto.SolunarPeriodResponse;
import com.lurepilot.backend.dto.WeatherHourlyResponse;
import com.lurepilot.backend.model.FishSpecies;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingPlanLure;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.model.WeatherSnapshot;
import com.lurepilot.backend.repository.FishSpeciesRepository;
import com.lurepilot.backend.repository.FishingPlanLureRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PlannerContextService {

    private static final int MAX_HOURLY_CONTEXT_ENTRIES = 6;

    private final FishingPlanRepository fishingPlanRepository;
    private final FishingPlanLureRepository fishingPlanLureRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final WeatherSnapshotRepository weatherSnapshotRepository;
    private final SolunarService solunarService;
    private final FishSpeciesRepository fishSpeciesRepository;
    private final LureLibraryItemRepository lureLibraryItemRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlannerContextService(
            FishingPlanRepository fishingPlanRepository,
            FishingPlanLureRepository fishingPlanLureRepository,
            FishingSessionRepository fishingSessionRepository,
            WeatherSnapshotRepository weatherSnapshotRepository,
            SolunarService solunarService,
            FishSpeciesRepository fishSpeciesRepository,
            LureLibraryItemRepository lureLibraryItemRepository
    ) {
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingPlanLureRepository = fishingPlanLureRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.weatherSnapshotRepository = weatherSnapshotRepository;
        this.solunarService = solunarService;
        this.fishSpeciesRepository = fishSpeciesRepository;
        this.lureLibraryItemRepository = lureLibraryItemRepository;
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

        List<PlannerContextResponse.PlannerContextSession> recentSpeciesSessions = findRecentSpeciesSessions(plan.getTargetSpecies(), spot)
                .stream()
                .map(this::toSessionContext)
                .toList();

        PlannerContextResponse.PlannerContextWeather weather = weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(planId)
                .map(snapshot -> toWeatherContext(snapshot, plan))
                .orElse(null);
        PlannerContextResponse.PlannerContextSolunar solunar = buildSolunarContext(plan, spot);

        List<PlannerContextResponse.PlannerContextFish> targetSpeciesProfiles = findTargetSpeciesProfiles(plan.getTargetSpecies(), spot);
        List<PlannerContextResponse.PlannerContextLibraryLure> availableLibraryLures = findAvailableLibraryLures(selectedLures);

        return new PlannerContextResponse(
                toPlanContext(plan),
                toSpotContext(spot),
                weather,
                solunar,
                selectedLures,
                recentSpotSessions,
                recentSpeciesSessions,
                buildDataQuality(weather, solunar, selectedLures, recentSpotSessions, recentSpeciesSessions, targetSpeciesProfiles, availableLibraryLures),
                targetSpeciesProfiles,
                availableLibraryLures,
                buildHistory(recentSpotSessions, recentSpeciesSessions)
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

    private PlannerContextResponse.PlannerContextFish toFishContext(FishSpecies fishSpecies) {
        return new PlannerContextResponse.PlannerContextFish(
                fishSpecies.getId(),
                fishSpecies.getName(),
                fishSpecies.getWaterEnvironment(),
                fishSpecies.getDescription(),
                fishSpecies.getHabitatNotes(),
                fishSpecies.getActiveTimes(),
                fishSpecies.getStrikeZone(),
                fishSpecies.getCommonZones(),
                fishSpecies.getFavoriteLures()
        );
    }

    private PlannerContextResponse.PlannerContextLibraryLure toLibraryLureContext(LureLibraryItem lure) {
        return new PlannerContextResponse.PlannerContextLibraryLure(
                lure.getId(),
                lure.getName(),
                lure.getType(),
                lure.getDifficulty(),
                lure.getEffectiveness(),
                lure.getDescription(),
                lure.getUsageNotes(),
                lure.getActionType(),
                lure.getIdealConditions()
        );
    }

    private List<PlannerContextResponse.PlannerContextFish> findTargetSpeciesProfiles(String targetSpecies, FishingSpot spot) {
        List<FishSpecies> allSpecies = fishSpeciesRepository.findAll();
        if (isAnySpecies(targetSpecies)) {
            Set<String> spotSpecies = new LinkedHashSet<>(splitValues(spot.getFavoriteSpecies()).stream()
                    .map(this::normalize)
                    .filter(name -> !name.isBlank())
                    .toList());

            return allSpecies.stream()
                    .filter(fish -> spotSpecies.contains(normalize(fish.getName())))
                    .limit(5)
                    .map(this::toFishContext)
                    .toList();
        }

        List<String> requestedNames = splitValues(targetSpecies);
        return allSpecies.stream()
                .filter(fish -> requestedNames.stream().anyMatch(name -> normalize(name).equals(normalize(fish.getName()))))
                .map(this::toFishContext)
                .toList();
    }

    private List<PlannerContextResponse.PlannerContextLibraryLure> findAvailableLibraryLures(
            List<PlannerContextResponse.PlannerContextLure> selectedLures
    ) {
        if (selectedLures.isEmpty()) {
            return List.of();
        }

        Set<Long> selectedLibraryIds = selectedLures.stream()
                .map(PlannerContextResponse.PlannerContextLure::libraryItemId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toSet());
        Set<String> selectedNames = selectedLures.stream()
                .flatMap(lure -> java.util.stream.Stream.of(lure.name(), lure.libraryItemName()))
                .map(this::normalize)
                .filter(name -> !name.isBlank())
                .collect(java.util.stream.Collectors.toSet());

        return lureLibraryItemRepository.findAll()
                .stream()
                .filter(lure -> selectedLibraryIds.contains(lure.getId()) || selectedNames.contains(normalize(lure.getName())))
                .map(this::toLibraryLureContext)
                .toList();
    }

    private List<FishingSession> findRecentSpeciesSessions(String targetSpecies, FishingSpot spot) {
        if (isAnySpecies(targetSpecies)) {
            return fishingSessionRepository.findTop5BySpotIdOrderByDateDescIdDesc(spot.getId());
        }

        Map<Long, FishingSession> matchesById = new LinkedHashMap<>();
        for (String speciesName : splitValues(targetSpecies)) {
            fishingSessionRepository.findTop5ByTargetSpeciesIgnoreCaseOrderByDateDescIdDesc(speciesName)
                    .forEach(session -> matchesById.putIfAbsent(session.getId(), session));
        }

        return matchesById.values()
                .stream()
                .sorted(Comparator.comparing(FishingSession::getDate, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(FishingSession::getId, Comparator.reverseOrder()))
                .limit(5)
                .toList();
    }

    private PlannerContextResponse.PlannerContextWeather toWeatherContext(WeatherSnapshot weatherSnapshot, FishingPlan plan) {
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
                weatherSnapshot.getNotes(),
                weatherSnapshot.getCurrentTemperature(),
                weatherSnapshot.getApparentTemperature(),
                weatherSnapshot.getRelativeHumidity(),
                weatherSnapshot.getPrecipitation(),
                weatherSnapshot.getPressureMsl(),
                weatherSnapshot.getCloudCover(),
                weatherSnapshot.getWindSpeedKmh(),
                weatherSnapshot.getWindGustsKmh(),
                weatherSnapshot.getSunrise(),
                weatherSnapshot.getSunset(),
                parseHourlyForecast(weatherSnapshot.getHourlyForecastJson(), plan.getPlannedDate(), plan.getPlannedTime())
        );
    }

    private List<WeatherHourlyResponse> parseHourlyForecast(String json, java.time.LocalDate plannedDate, java.time.LocalTime plannedTime) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            List<WeatherHourlyResponse> forecast = objectMapper.readValue(json, new TypeReference<>() {
            });
            if (forecast.size() <= MAX_HOURLY_CONTEXT_ENTRIES || plannedDate == null || plannedTime == null) {
                return forecast.stream().limit(MAX_HOURLY_CONTEXT_ENTRIES).toList();
            }

            LocalDateTime plannedAt = LocalDateTime.of(plannedDate, plannedTime);
            return forecast.stream()
                    .sorted(Comparator.comparingLong(hour -> distanceFromPlannedTime(hour, plannedAt)))
                    .limit(MAX_HOURLY_CONTEXT_ENTRIES)
                    .sorted(Comparator.comparing(WeatherHourlyResponse::time, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
        } catch (Exception exception) {
            return List.of();
        }
    }

    private long distanceFromPlannedTime(WeatherHourlyResponse hour, LocalDateTime plannedAt) {
        try {
            return Math.abs(Duration.between(plannedAt, LocalDateTime.parse(hour.time())).toMinutes());
        } catch (Exception exception) {
            return Long.MAX_VALUE;
        }
    }

    private PlannerContextResponse.PlannerContextSolunar buildSolunarContext(FishingPlan plan, FishingSpot spot) {
        if (spot == null || spot.getId() == null || spot.getLatitude() == null || spot.getLongitude() == null) {
            return null;
        }

        try {
            SolunarForecastResponse forecast = solunarService.getForecast(spot.getId(), plan.getPlannedDate());
            return new PlannerContextResponse.PlannerContextSolunar(
                    forecast.spotId(),
                    forecast.date(),
                    forecast.timezone(),
                    forecast.sunrise(),
                    forecast.sunset(),
                    forecast.moonrise(),
                    forecast.moonset(),
                    forecast.moonPhase(),
                    forecast.moonIlluminationPercent(),
                    forecast.activityLevel(),
                    toSolunarPeriods(forecast.majorPeriods()),
                    toSolunarPeriods(forecast.minorPeriods()),
                    forecast.note()
            );
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private List<PlannerContextResponse.PlannerContextSolunarPeriod> toSolunarPeriods(List<SolunarPeriodResponse> periods) {
        if (periods == null) {
            return List.of();
        }

        return periods.stream()
                .map(period -> new PlannerContextResponse.PlannerContextSolunarPeriod(
                        period.type(),
                        period.title(),
                        period.peakAt(),
                        period.startsAt(),
                        period.endsAt()
                ))
                .toList();
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
            PlannerContextResponse.PlannerContextSolunar solunar,
            List<PlannerContextResponse.PlannerContextLure> selectedLures,
            List<PlannerContextResponse.PlannerContextSession> recentSpotSessions,
            List<PlannerContextResponse.PlannerContextSession> recentSpeciesSessions,
            List<PlannerContextResponse.PlannerContextFish> targetSpeciesProfiles,
            List<PlannerContextResponse.PlannerContextLibraryLure> availableLibraryLures
    ) {
        List<String> warnings = new ArrayList<>();

        if (selectedLures.isEmpty()) {
            warnings.add("No lures selected for this fishing plan yet.");
        }

        if (weather == null) {
            warnings.add("No weather snapshot found for this fishing plan.");
        }

        if (solunar == null) {
            warnings.add("No solunar forecast available for this fishing plan.");
        }

        if (recentSpotSessions.isEmpty()) {
            warnings.add("No recent history found for this spot.");
        }

        if (recentSpeciesSessions.isEmpty()) {
            warnings.add("No recent history found for this target species.");
        }

        if (targetSpeciesProfiles.isEmpty()) {
            warnings.add("No matching species profile found in the fish library.");
        }

        if (availableLibraryLures.isEmpty()) {
            warnings.add("No lure library information is available.");
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

    private PlannerContextResponse.PlannerContextHistory buildHistory(
            List<PlannerContextResponse.PlannerContextSession> recentSpotSessions,
            List<PlannerContextResponse.PlannerContextSession> recentSpeciesSessions
    ) {
        return new PlannerContextResponse.PlannerContextHistory(
                recentSpotSessions.size(),
                recentSpeciesSessions.size(),
                countSuccessful(recentSpotSessions),
                countSuccessful(recentSpeciesSessions),
                successRate(recentSpotSessions),
                successRate(recentSpeciesSessions)
        );
    }

    private int countSuccessful(List<PlannerContextResponse.PlannerContextSession> sessions) {
        return (int) sessions.stream().filter(session -> Boolean.TRUE.equals(session.success())).count();
    }

    private Double successRate(List<PlannerContextResponse.PlannerContextSession> sessions) {
        if (sessions.isEmpty()) {
            return null;
        }

        return countSuccessful(sessions) * 100.0 / sessions.size();
    }

    private boolean isAnySpecies(String targetSpecies) {
        String normalized = normalize(targetSpecies);
        return normalized.contains("any species") || normalized.contains("qualquer especie") || normalized.equals("any");
    }

    private List<String> splitValues(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        return List.of(value.split("[,;\\n|]+"));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
