package com.lurepilot.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PlannerContextResponse(
        PlannerContextPlan plan,
        PlannerContextSpot spot,
        PlannerContextWeather weather,
        PlannerContextSolunar solunar,
        List<PlannerContextLure> selectedLures,
        List<PlannerContextSession> recentSpotSessions,
        List<PlannerContextSession> recentSpeciesSessions,
        PlannerContextDataQuality dataQuality,
        List<PlannerContextFish> targetSpeciesProfiles,
        List<PlannerContextLibraryLure> availableLibraryLures,
        PlannerContextHistory history
) {

    public PlannerContextResponse(
            PlannerContextPlan plan,
            PlannerContextSpot spot,
            PlannerContextWeather weather,
            List<PlannerContextLure> selectedLures,
            List<PlannerContextSession> recentSpotSessions,
            List<PlannerContextSession> recentSpeciesSessions,
            PlannerContextDataQuality dataQuality
    ) {
        this(
                plan,
                spot,
                weather,
                null,
                selectedLures,
                recentSpotSessions,
                recentSpeciesSessions,
                dataQuality,
                List.of(),
                List.of(),
                new PlannerContextHistory(0, 0, 0, 0, null, null)
        );
    }

    public record PlannerContextPlan(
            Long id,
            LocalDate plannedDate,
            LocalTime plannedTime,
            String targetSpecies,
            String waterClarity,
            String waterLevel,
            String notes
    ) {
    }

    public record PlannerContextSpot(
            Long id,
            String name,
            String description,
            Double latitude,
            Double longitude,
            String waterType,
            String favoriteSpecies
    ) {
    }

    public record PlannerContextLure(
            Long id,
            String name,
            String type,
            String color,
            String size,
            Double weight,
            String brand,
            String targetSpecies,
            String waterType,
            Long libraryItemId,
            String libraryItemName
    ) {
    }

    public record PlannerContextFish(
            Long id,
            String name,
            String waterEnvironment,
            String description,
            String habitatNotes,
            String activeTimes,
            String strikeZone,
            String commonZones,
            String favoriteLures
    ) {
    }

    public record PlannerContextLibraryLure(
            Long id,
            String name,
            String type,
            String difficulty,
            String effectiveness,
            String description,
            String usageNotes,
            String actionType,
            String idealConditions
    ) {
    }

    public record PlannerContextWeather(
            Long id,
            String source,
            String sourceLocationName,
            java.time.LocalDate forecastDate,
            Double temperatureMin,
            Double temperatureMax,
            Double precipitationProbability,
            String windDirection,
            Integer windSpeedClass,
            String notes,
            Double currentTemperature,
            Double apparentTemperature,
            Double relativeHumidity,
            Double precipitation,
            Double pressureMsl,
            Integer cloudCover,
            Double windSpeedKmh,
            Double windGustsKmh,
            String sunrise,
            String sunset,
            List<WeatherHourlyResponse> hourlyForecast
    ) {
        public PlannerContextWeather(
                Long id,
                String source,
                String sourceLocationName,
                LocalDate forecastDate,
                Double temperatureMin,
                Double temperatureMax,
                Double precipitationProbability,
                String windDirection,
                Integer windSpeedClass,
                String notes
        ) {
            this(
                    id,
                    source,
                    sourceLocationName,
                    forecastDate,
                    temperatureMin,
                    temperatureMax,
                    precipitationProbability,
                    windDirection,
                    windSpeedClass,
                    notes,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of()
            );
        }
    }

    public record PlannerContextSolunar(
            Long spotId,
            LocalDate date,
            String timezone,
            String sunrise,
            String sunset,
            String moonrise,
            String moonset,
            String moonPhase,
            Double moonIlluminationPercent,
            String activityLevel,
            List<PlannerContextSolunarPeriod> majorPeriods,
            List<PlannerContextSolunarPeriod> minorPeriods,
            String note
    ) {
    }

    public record PlannerContextSolunarPeriod(
            String type,
            String title,
            String peakAt,
            String startsAt,
            String endsAt
    ) {
    }

    public record PlannerContextSession(
            Long id,
            Long spotId,
            String spotName,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String targetSpecies,
            String waterClarity,
            String waterLevel,
            String status,
            Boolean success,
            Long durationMinutes,
            String resultSummary,
            Integer rating,
            String notes
    ) {
    }

    public record PlannerContextDataQuality(
            int selectedLureCount,
            int recentSpotSessionCount,
            int recentSpeciesSessionCount,
            String confidenceHint,
            List<String> warnings
    ) {
    }

    public record PlannerContextHistory(
            int spotSessionCount,
            int speciesSessionCount,
            int successfulSpotSessionCount,
            int successfulSpeciesSessionCount,
            Double spotSuccessRate,
            Double speciesSuccessRate
    ) {
    }
}
