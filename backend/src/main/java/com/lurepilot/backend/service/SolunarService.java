package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.SolunarForecastResponse;
import com.lurepilot.backend.dto.SolunarPeriodResponse;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.FishingSpotRepository;
import org.shredzone.commons.suncalc.MoonIllumination;
import org.shredzone.commons.suncalc.MoonPosition;
import org.shredzone.commons.suncalc.MoonTimes;
import org.shredzone.commons.suncalc.SunTimes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SolunarService {

    private static final ZoneId MAINLAND_TIMEZONE = ZoneId.of("Europe/Lisbon");
    private static final ZoneId MADEIRA_TIMEZONE = ZoneId.of("Atlantic/Madeira");
    private static final ZoneId AZORES_TIMEZONE = ZoneId.of("Atlantic/Azores");
    private static final DateTimeFormatter API_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX");
    private static final Duration PERIOD_HALF_LENGTH = Duration.ofMinutes(60);

    private final FishingSpotRepository fishingSpotRepository;

    public SolunarService(FishingSpotRepository fishingSpotRepository) {
        this.fishingSpotRepository = fishingSpotRepository;
    }

    public SolunarForecastResponse getForecast(Long spotId, LocalDate requestedDate) {
        FishingSpot spot = fishingSpotRepository.findById(spotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing spot not found"));

        return getForecast(spot.getId(), spot.getName(), spot.getLatitude(), spot.getLongitude(), requestedDate);
    }

    public SolunarForecastResponse getForecast(
            Double latitude,
            Double longitude,
            String locationName,
            LocalDate requestedDate
    ) {
        validateCoordinates(latitude, longitude);
        return getForecast(null, locationName, latitude, longitude, requestedDate);
    }

    private SolunarForecastResponse getForecast(
            Long spotId,
            String locationName,
            Double latitude,
            Double longitude,
            LocalDate requestedDate
    ) {
        validateCoordinates(latitude, longitude);

        LocalDate date = requestedDate == null ? LocalDate.now() : requestedDate;
        ZoneId timezone = timezoneFor(latitude, longitude);
        ZonedDateTime startOfDay = date.atStartOfDay(timezone);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        SunTimes sunTimes = SunTimes.compute()
                .on(startOfDay)
                .at(latitude, longitude)
                .timezone(timezone)
                .oneDay()
                .execute();
        MoonTimes moonTimes = MoonTimes.compute()
                .on(startOfDay)
                .at(latitude, longitude)
                .timezone(timezone)
                .oneDay()
                .execute();
        MoonIllumination moonIllumination = MoonIllumination.compute()
                .on(startOfDay.plusHours(12))
                .at(latitude, longitude)
                .timezone(timezone)
                .execute();

        ZonedDateTime moonrise = normalizeEvent(moonTimes.getRise(), startOfDay, endOfDay);
        ZonedDateTime moonset = normalizeEvent(moonTimes.getSet(), startOfDay, endOfDay);
        List<SolunarPeriodResponse> majorPeriods = findMajorPeriods(startOfDay, endOfDay, latitude, longitude, timezone);
        List<SolunarPeriodResponse> minorPeriods = buildMinorPeriods(moonrise, moonset, startOfDay, endOfDay);

        return new SolunarForecastResponse(
                spotId,
                locationName,
                date,
                timezone.getId(),
                format(sunTimes.getRise()),
                format(sunTimes.getSet()),
                format(moonrise),
                format(moonset),
                moonIllumination.getClosestPhase().name(),
                round(moonIllumination.getFraction() * 100.0),
                majorPeriods,
                minorPeriods,
                activityLevel(moonIllumination.getFraction()),
                "Indicador tradicional de atividade solunar; não é garantia de captura."
        );
    }

    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solunar forecasts require valid coordinates");
        }
    }

    private List<SolunarPeriodResponse> findMajorPeriods(
            ZonedDateTime startOfDay,
            ZonedDateTime endOfDay,
            Double latitude,
            Double longitude,
            ZoneId timezone
    ) {
        List<MoonSample> samples = new ArrayList<>();
        for (ZonedDateTime time = startOfDay; time.isBefore(endOfDay); time = time.plusMinutes(30)) {
            MoonPosition position = MoonPosition.compute()
                    .on(time)
                    .at(latitude, longitude)
                    .timezone(timezone)
                    .execute();
            samples.add(new MoonSample(time, position.getAltitude()));
        }

        MoonSample upperTransit = samples.stream().max(Comparator.comparingDouble(MoonSample::altitude)).orElse(null);
        MoonSample lowerTransit = samples.stream().min(Comparator.comparingDouble(MoonSample::altitude)).orElse(null);
        List<SolunarPeriodResponse> periods = new ArrayList<>();
        addPeriod(periods, "UPPER_TRANSIT", "Trânsito lunar superior", upperTransit, startOfDay, endOfDay);
        addPeriod(periods, "LOWER_TRANSIT", "Trânsito lunar inferior", lowerTransit, startOfDay, endOfDay);
        return periods;
    }

    private List<SolunarPeriodResponse> buildMinorPeriods(
            ZonedDateTime moonrise,
            ZonedDateTime moonset,
            ZonedDateTime startOfDay,
            ZonedDateTime endOfDay
    ) {
        List<SolunarPeriodResponse> periods = new ArrayList<>();
        addPeriod(periods, "MOONRISE", "Nascer da Lua", moonrise, startOfDay, endOfDay);
        addPeriod(periods, "MOONSET", "Pôr da Lua", moonset, startOfDay, endOfDay);
        return periods;
    }

    private void addPeriod(
            List<SolunarPeriodResponse> periods,
            String type,
            String title,
            MoonSample sample,
            ZonedDateTime startOfDay,
            ZonedDateTime endOfDay
    ) {
        if (sample != null) {
            addPeriod(periods, type, title, sample.time(), startOfDay, endOfDay);
        }
    }

    private void addPeriod(
            List<SolunarPeriodResponse> periods,
            String type,
            String title,
            ZonedDateTime peak,
            ZonedDateTime startOfDay,
            ZonedDateTime endOfDay
    ) {
        if (peak == null) {
            return;
        }

        ZonedDateTime startsAt = peak.minus(PERIOD_HALF_LENGTH).isBefore(startOfDay)
                ? startOfDay
                : peak.minus(PERIOD_HALF_LENGTH);
        ZonedDateTime endsAt = peak.plus(PERIOD_HALF_LENGTH).isAfter(endOfDay)
                ? endOfDay
                : peak.plus(PERIOD_HALF_LENGTH);
        periods.add(new SolunarPeriodResponse(type, title, format(peak), format(startsAt), format(endsAt)));
    }

    private ZonedDateTime normalizeEvent(ZonedDateTime event, ZonedDateTime startOfDay, ZonedDateTime endOfDay) {
        if (event == null) {
            return null;
        }

        return event.isBefore(startOfDay) || !event.isBefore(endOfDay) ? null : event;
    }

    private ZoneId timezoneFor(Double latitude, Double longitude) {
        if (longitude <= -24) {
            return AZORES_TIMEZONE;
        }
        if (longitude <= -15 && latitude < 35) {
            return MADEIRA_TIMEZONE;
        }
        return MAINLAND_TIMEZONE;
    }

    private String activityLevel(double fraction) {
        return fraction <= 0.15 || fraction >= 0.85 ? "HIGH" : fraction <= 0.3 || fraction >= 0.7 ? "MEDIUM" : "LOW";
    }

    private String format(ZonedDateTime value) {
        return value == null ? null : value.format(API_TIME_FORMAT);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private record MoonSample(ZonedDateTime time, double altitude) {
    }
}
