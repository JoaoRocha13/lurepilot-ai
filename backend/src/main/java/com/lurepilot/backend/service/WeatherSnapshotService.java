package com.lurepilot.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lurepilot.backend.client.OpenMeteoClient;
import com.lurepilot.backend.dto.CreateWeatherCoordinateSnapshotRequest;
import com.lurepilot.backend.dto.CreateWeatherLocationSnapshotRequest;
import com.lurepilot.backend.dto.WeatherHourlyResponse;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class WeatherSnapshotService {

    private static final String SOURCE_OPEN_METEO = "OPEN_METEO";
    private static final TypeReference<List<WeatherHourlyResponse>> HOURLY_TYPE = new TypeReference<>() {
    };

    private final WeatherSnapshotRepository weatherSnapshotRepository;
    private final FishingPlanRepository fishingPlanRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final OpenMeteoClient openMeteoClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherSnapshotService(
            WeatherSnapshotRepository weatherSnapshotRepository,
            FishingPlanRepository fishingPlanRepository,
            FishingSessionRepository fishingSessionRepository,
            OpenMeteoClient openMeteoClient
    ) {
        this.weatherSnapshotRepository = weatherSnapshotRepository;
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.openMeteoClient = openMeteoClient;
    }

    public WeatherSnapshotResponse createSnapshotForPlan(Long planId) {
        FishingPlan plan = fishingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing plan not found"));

        return toResponse(createSnapshot(plan, null, plan.getSpot(), plan.getPlannedDate(), null));
    }

    public WeatherSnapshotResponse createSnapshotForSession(Long sessionId) {
        FishingSession session = fishingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));

        return toResponse(createSnapshot(session.getPlan(), session, session.getSpot(), session.getDate(), null));
    }

    public WeatherSnapshotResponse createSnapshotForCoordinates(CreateWeatherCoordinateSnapshotRequest request) {
        return toResponse(createSnapshot(
                null,
                null,
                "Coordenadas selecionadas",
                request.latitude(),
                request.longitude(),
                request.forecastDate(),
                null
        ));
    }

    public WeatherSnapshotResponse createSnapshotForLocation(CreateWeatherLocationSnapshotRequest request) {
        return toResponse(createSnapshot(
                null,
                null,
                request.name(),
                request.latitude(),
                request.longitude(),
                request.forecastDate(),
                request.locationId()
        ));
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

    private WeatherSnapshot createSnapshot(FishingPlan plan, FishingSession session, FishingSpot spot, LocalDate targetDate, Integer sourceLocationId) {
        if (spot == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Weather snapshots require a fishing spot with coordinates");
        }

        return createSnapshot(plan, session, spot.getName(), spot.getLatitude(), spot.getLongitude(), targetDate, sourceLocationId);
    }

    private WeatherSnapshot createSnapshot(FishingPlan plan, FishingSession session, String locationName, Double latitude, Double longitude, LocalDate targetDate, Integer sourceLocationId) {
        if (latitude == null || longitude == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Weather snapshots require latitude and longitude");
        }

        try {
            OpenMeteoClient.ForecastResponse forecast = openMeteoClient.getForecast(latitude, longitude);
            DailySelection selection = selectDailyForecast(forecast.daily(), targetDate);
            OpenMeteoClient.DailyWeather daily = forecast.daily();
            OpenMeteoClient.CurrentWeather current = forecast.current();
            List<WeatherHourlyResponse> hourly = selectHourlyForecast(forecast.hourly(), selection.forecastDate());
            Double windSpeedKmh = current == null ? valueAt(daily.windSpeedMax(), selection.index()) : current.windSpeed();
            Double windGustsKmh = current == null ? valueAt(daily.windGustsMax(), selection.index()) : current.windGusts();

            WeatherSnapshot weatherSnapshot = new WeatherSnapshot(
                    plan,
                    session,
                    SOURCE_OPEN_METEO,
                    sourceLocationId,
                    locationName,
                    latitude,
                    longitude,
                    selection.forecastDate(),
                    Instant.now(),
                    valueAt(daily.weatherCode(), selection.index()),
                    valueAt(daily.temperatureMin(), selection.index()),
                    valueAt(daily.temperatureMax(), selection.index()),
                    valueAt(daily.precipitationProbabilityMax(), selection.index()),
                    toWindDirection(valueAt(daily.windDirection(), selection.index())),
                    toWindSpeedClass(windSpeedKmh),
                    current == null ? null : current.temperature(),
                    current == null ? null : current.apparentTemperature(),
                    current == null ? null : current.relativeHumidity(),
                    current == null ? valueAt(daily.precipitationSum(), selection.index()) : current.precipitation(),
                    current == null ? null : current.pressureMsl(),
                    current == null ? null : current.cloudCover(),
                    windSpeedKmh,
                    windGustsKmh,
                    valueAt(daily.sunrise(), selection.index()),
                    valueAt(daily.sunset(), selection.index()),
                    serializeHourlyForecast(hourly),
                    buildNotes(locationName, targetDate, selection.usedExactDate())
            );

            return weatherSnapshotRepository.save(weatherSnapshot);
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not create weather snapshot from Open-Meteo", exception);
        }
    }

    private DailySelection selectDailyForecast(OpenMeteoClient.DailyWeather daily, LocalDate targetDate) {
        if (daily == null || daily.time() == null || daily.time().isEmpty()) {
            throw new IllegalStateException("Open-Meteo returned no daily forecast data");
        }

        int index = 0;
        boolean exactDate = false;
        if (targetDate != null) {
            int requestedIndex = daily.time().indexOf(targetDate.toString());
            if (requestedIndex >= 0) {
                index = requestedIndex;
                exactDate = true;
            } else {
                index = nearestDateIndex(daily.time(), targetDate);
            }
        }

        return new DailySelection(LocalDate.parse(daily.time().get(index)), index, exactDate);
    }

    private int nearestDateIndex(List<String> dates, LocalDate targetDate) {
        return dates.stream()
                .map(LocalDate::parse)
                .min(Comparator.comparingLong(date -> Math.abs(date.toEpochDay() - targetDate.toEpochDay())))
                .map(dates::indexOf)
                .orElse(0);
    }

    private List<WeatherHourlyResponse> selectHourlyForecast(OpenMeteoClient.HourlyWeather hourly, LocalDate forecastDate) {
        if (hourly == null || hourly.time() == null) {
            return List.of();
        }

        List<WeatherHourlyResponse> result = new ArrayList<>();
        for (int index = 0; index < hourly.time().size(); index++) {
            String time = hourly.time().get(index);
            if (!time.startsWith(forecastDate.toString())) {
                continue;
            }

            result.add(new WeatherHourlyResponse(
                    time,
                    valueAt(hourly.temperature(), index),
                    valueAt(hourly.relativeHumidity(), index),
                    valueAt(hourly.precipitationProbability(), index),
                    valueAt(hourly.precipitation(), index),
                    valueAt(hourly.weatherCode(), index),
                    valueAt(hourly.windSpeed(), index),
                    valueAt(hourly.windDirection(), index),
                    valueAt(hourly.windGusts(), index)
            ));
        }

        return result;
    }

    private String serializeHourlyForecast(List<WeatherHourlyResponse> hourly) {
        try {
            return objectMapper.writeValueAsString(hourly);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not store hourly weather forecast", exception);
        }
    }

    private List<WeatherHourlyResponse> deserializeHourlyForecast(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(json, HOURLY_TYPE);
        } catch (JsonProcessingException exception) {
            return List.of();
        }
    }

    private String buildNotes(String locationName, LocalDate targetDate, boolean exactDate) {
        String base = "Open-Meteo forecast fetched for " + locationName + " coordinates.";
        if (targetDate == null || exactDate) {
            return base;
        }

        return base + " The requested date was outside the available forecast window, so the closest forecast date was saved.";
    }

    private WeatherSnapshotResponse toResponse(WeatherSnapshot weatherSnapshot) {
        FishingPlan plan = weatherSnapshot.getPlan();
        FishingSession session = weatherSnapshot.getSession();

        return new WeatherSnapshotResponse(
                weatherSnapshot.getId(),
                plan == null ? null : plan.getId(),
                session == null ? null : session.getId(),
                weatherSnapshot.getSource(),
                weatherSnapshot.getSourceLocationId(),
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
                deserializeHourlyForecast(weatherSnapshot.getHourlyForecastJson()),
                weatherSnapshot.getNotes(),
                weatherSnapshot.getCapturedAt()
        );
    }

    private String toWindDirection(Integer degrees) {
        if (degrees == null) {
            return null;
        }

        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int) Math.floor((degrees + 22.5) / 45.0) % directions.length;
        return directions[index];
    }

    private Integer toWindSpeedClass(Double speedKmh) {
        if (speedKmh == null) {
            return null;
        }

        return Math.min(5, Math.max(0, (int) Math.ceil(speedKmh / 10.0)));
    }

    private <T> T valueAt(List<T> values, int index) {
        if (values == null || index < 0 || index >= values.size()) {
            return null;
        }

        return values.get(index);
    }

    private record DailySelection(LocalDate forecastDate, int index, boolean usedExactDate) {
    }
}
