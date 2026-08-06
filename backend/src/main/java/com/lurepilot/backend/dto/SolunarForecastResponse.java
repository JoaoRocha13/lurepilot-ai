package com.lurepilot.backend.dto;

import java.time.LocalDate;
import java.util.List;

public record SolunarForecastResponse(
        Long spotId,
        String spotName,
        LocalDate date,
        String timezone,
        String sunrise,
        String sunset,
        String moonrise,
        String moonset,
        String moonPhase,
        Double moonIlluminationPercent,
        List<SolunarPeriodResponse> majorPeriods,
        List<SolunarPeriodResponse> minorPeriods,
        String activityLevel,
        String note
) {
}
