package com.lurepilot.backend.service;

import com.lurepilot.backend.client.IpmaClient;
import com.lurepilot.backend.dto.CreateIpmaCoordinateSnapshotRequest;
import com.lurepilot.backend.dto.CreateIpmaLocationSnapshotRequest;
import com.lurepilot.backend.dto.WeatherSnapshotResponse;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.WeatherSnapshot;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class WeatherSnapshotService {

    private static final String SOURCE_IPMA = "IPMA";

    private final WeatherSnapshotRepository weatherSnapshotRepository;
    private final FishingPlanRepository fishingPlanRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final IpmaClient ipmaClient;

    public WeatherSnapshotService(
            WeatherSnapshotRepository weatherSnapshotRepository,
            FishingPlanRepository fishingPlanRepository,
            FishingSessionRepository fishingSessionRepository,
            IpmaClient ipmaClient
    ) {
        this.weatherSnapshotRepository = weatherSnapshotRepository;
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.ipmaClient = ipmaClient;
    }

    public WeatherSnapshotResponse createIpmaSnapshotForPlan(Long planId) {
        FishingPlan plan = fishingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));

        return toResponse(createIpmaSnapshot(plan, null, plan.getSpot(), plan.getPlannedDate()));
    }

    public WeatherSnapshotResponse createIpmaSnapshotForSession(Long sessionId) {
        FishingSession session = fishingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));

        return toResponse(createIpmaSnapshot(session.getPlan(), session, session.getSpot(), session.getDate()));
    }

    public WeatherSnapshotResponse createIpmaSnapshotForCoordinates(CreateIpmaCoordinateSnapshotRequest request) {
        return toResponse(createIpmaSnapshot(
                null,
                null,
                "Custom coordinates",
                request.latitude(),
                request.longitude(),
                request.forecastDate()
        ));
    }

    public WeatherSnapshotResponse createIpmaSnapshotForLocation(CreateIpmaLocationSnapshotRequest request) {
        IpmaClient.IpmaLocation location = findLocationByGlobalId(request.globalIdLocal());
        return toResponse(createIpmaSnapshotForIpmaLocation(null, null, location, request.forecastDate(), "IPMA location selected by user."));
    }

    public List<WeatherSnapshotResponse> getSnapshotsByPlan(Long planId) {
        if (!fishingPlanRepository.existsById(planId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found");
        }

        return weatherSnapshotRepository.findByPlanIdOrderByCapturedAtDescIdDesc(planId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<WeatherSnapshotResponse> getSnapshotsBySession(Long sessionId) {
        if (!fishingSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        return weatherSnapshotRepository.findBySessionIdOrderByCapturedAtDescIdDesc(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WeatherSnapshotResponse getLatestSnapshotForPlan(Long planId) {
        return weatherSnapshotRepository.findFirstByPlanIdOrderByCapturedAtDescIdDesc(planId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Weather snapshot not found"));
    }

    public WeatherSnapshotResponse getLatestSnapshotForSession(Long sessionId) {
        return weatherSnapshotRepository.findFirstBySessionIdOrderByCapturedAtDescIdDesc(sessionId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Weather snapshot not found"));
    }

    private WeatherSnapshot createIpmaSnapshot(FishingPlan plan, FishingSession session, FishingSpot spot, LocalDate targetDate) {
        return createIpmaSnapshot(plan, session, spot.getName(), spot.getLatitude(), spot.getLongitude(), targetDate);
    }

    private WeatherSnapshot createIpmaSnapshot(FishingPlan plan, FishingSession session, String locationLabel, Double latitude, Double longitude, LocalDate targetDate) {
        try {
            IpmaClient.IpmaLocation nearestLocation = findNearestLocation(latitude, longitude);
            String notes = "Local IPMA selected automatically from " + locationLabel + " coordinates.";
            return createIpmaSnapshotForIpmaLocation(plan, session, nearestLocation, targetDate, notes);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not create weather snapshot from IPMA", ex);
        }
    }

    private WeatherSnapshot createIpmaSnapshotForIpmaLocation(FishingPlan plan, FishingSession session, IpmaClient.IpmaLocation location, LocalDate targetDate, String baseNotes) {
        try {
            IpmaClient.IpmaForecastResponse forecast = ipmaClient.getDailyForecast(location.globalIdLocal());
            IpmaForecastSelection forecastSelection = selectForecastDay(forecast.data(), targetDate);
            IpmaClient.IpmaForecastDay forecastDay = forecastSelection.forecastDay();

            WeatherSnapshot weatherSnapshot = new WeatherSnapshot(
                    plan,
                    session,
                    SOURCE_IPMA,
                    location.globalIdLocal(),
                    location.local(),
                    parseDouble(location.latitude()),
                    parseDouble(location.longitude()),
                    forecastDay.forecastDate(),
                    forecast.dataUpdate(),
                    forecastDay.idWeatherType(),
                    parseDouble(forecastDay.tMin()),
                    parseDouble(forecastDay.tMax()),
                    parseDouble(firstNonBlank(forecastDay.precipitaProb(), forecastDay.probPrecipita())),
                    forecastDay.predWindDir(),
                    forecastDay.classWindSpeed(),
                    buildNotes(baseNotes, targetDate, forecastSelection.usedExactDate())
            );

            return weatherSnapshotRepository.save(weatherSnapshot);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not create weather snapshot from IPMA", ex);
        }
    }

    private IpmaClient.IpmaLocation findNearestLocation(Double latitude, Double longitude) {
        return ipmaClient.getLocations()
                .stream()
                .min(Comparator.comparingDouble(location -> distanceSquared(
                        latitude,
                        longitude,
                        parseDouble(location.latitude()),
                        parseDouble(location.longitude())
                )))
                .orElseThrow(() -> new IllegalStateException("No IPMA location found"));
    }

    private IpmaClient.IpmaLocation findLocationByGlobalId(Integer globalIdLocal) {
        return ipmaClient.getLocations()
                .stream()
                .filter(location -> globalIdLocal.equals(location.globalIdLocal()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "IPMA location not found"));
    }

    private IpmaForecastSelection selectForecastDay(List<IpmaClient.IpmaForecastDay> forecastDays, LocalDate targetDate) {
        return forecastDays.stream()
                .filter(forecastDay -> targetDate != null && targetDate.equals(forecastDay.forecastDate()))
                .findFirst()
                .map(forecastDay -> new IpmaForecastSelection(forecastDay, true))
                .orElseGet(() -> new IpmaForecastSelection(forecastDays.getFirst(), false));
    }

    private String buildNotes(String base, LocalDate targetDate, boolean usedExactDate) {
        if (usedExactDate || targetDate == null) {
            return base;
        }

        return base + " Target date is outside the available IPMA forecast window, so the closest available daily forecast was saved.";
    }

    private WeatherSnapshotResponse toResponse(WeatherSnapshot weatherSnapshot) {
        FishingPlan plan = weatherSnapshot.getPlan();
        FishingSession session = weatherSnapshot.getSession();

        return new WeatherSnapshotResponse(
                weatherSnapshot.getId(),
                plan == null ? null : plan.getId(),
                session == null ? null : session.getId(),
                weatherSnapshot.getSource(),
                weatherSnapshot.getSourceGlobalIdLocal(),
                weatherSnapshot.getSourceLocationName(),
                weatherSnapshot.getSourceLatitude(),
                weatherSnapshot.getSourceLongitude(),
                weatherSnapshot.getForecastDate(),
                weatherSnapshot.getDataUpdate(),
                weatherSnapshot.getWeatherTypeId(),
                weatherSnapshot.getTemperatureMin(),
                weatherSnapshot.getTemperatureMax(),
                weatherSnapshot.getPrecipitationProbability(),
                weatherSnapshot.getWindDirection(),
                weatherSnapshot.getWindSpeedClass(),
                weatherSnapshot.getNotes(),
                weatherSnapshot.getCapturedAt()
        );
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Double.parseDouble(value);
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }

        return second;
    }

    private double distanceSquared(Double latitudeA, Double longitudeA, Double latitudeB, Double longitudeB) {
        double latitudeDifference = latitudeA - latitudeB;
        double longitudeDifference = longitudeA - longitudeB;
        return latitudeDifference * latitudeDifference + longitudeDifference * longitudeDifference;
    }

    private record IpmaForecastSelection(IpmaClient.IpmaForecastDay forecastDay, boolean usedExactDate) {
    }
}
