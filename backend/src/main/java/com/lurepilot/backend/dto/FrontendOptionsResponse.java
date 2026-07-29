package com.lurepilot.backend.dto;

import java.util.List;

public record FrontendOptionsResponse(
        List<OptionResponse> waterTypes,
        List<OptionResponse> waterClarities,
        List<OptionResponse> waterLevels,
        List<OptionResponse> lureTypes,
        List<OptionResponse> lureDifficulties,
        List<OptionResponse> lureEffectivenessLevels,
        List<OptionResponse> lureConditions,
        List<OptionResponse> fishStrikeZones,
        List<OptionResponse> sessionStatuses,
        List<OptionResponse> sessionEventTypes,
        List<OptionResponse> recommendationTypes,
        List<OptionResponse> recommendationSteps,
        List<OptionResponse> recommendationResults,
        List<OptionResponse> sortDirections
) {
}
