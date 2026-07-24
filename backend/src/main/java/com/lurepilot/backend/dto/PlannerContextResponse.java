package com.lurepilot.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PlannerContextResponse(
        PlannerContextPlan plan,
        PlannerContextSpot spot,
        PlannerContextWeather weather,
        List<PlannerContextLure> selectedLures,
        List<PlannerContextSession> recentSpotSessions,
        List<PlannerContextSession> recentSpeciesSessions,
        PlannerContextDataQuality dataQuality
) {

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
            String notes
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
}
