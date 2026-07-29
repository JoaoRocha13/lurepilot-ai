package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.FrontendOptionsResponse;
import com.lurepilot.backend.dto.OptionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontendOptionsService {

    private static final List<OptionResponse> WATER_TYPES = List.of(
            option("Freshwater", "Agua doce"),
            option("River", "Rio"),
            option("Reservoir", "Albufeira"),
            option("Lake", "Lago"),
            option("Pond", "Charca"),
            option("Sea", "Mar"),
            option("Estuary", "Estuario")
    );

    private static final List<OptionResponse> WATER_CLARITIES = List.of(
            option("Clear", "Clara"),
            option("Slightly stained", "Ligeiramente turva"),
            option("Stained", "Turva"),
            option("Muddy", "Muito turva")
    );

    private static final List<OptionResponse> WATER_LEVELS = List.of(
            option("Low", "Baixo"),
            option("Normal", "Normal"),
            option("High", "Alto"),
            option("Rising", "A subir"),
            option("Falling", "A descer")
    );

    private static final List<OptionResponse> LURE_TYPES = List.of(
            option("Soft bait", "Vinil / soft bait"),
            option("Crankbait", "Crankbait"),
            option("Topwater", "Superficie"),
            option("Jerkbait", "Jerkbait"),
            option("Spinnerbait", "Spinnerbait"),
            option("Jig", "Jig"),
            option("Swimbait", "Swimbait"),
            option("Spoon", "Colher"),
            option("Spinner", "Spinner"),
            option("Frog", "Frog")
    );

    private static final List<OptionResponse> LURE_DIFFICULTIES = List.of(
            option("Easy", "Facil"),
            option("Medium", "Media"),
            option("Hard", "Dificil")
    );

    private static final List<OptionResponse> LURE_EFFECTIVENESS_LEVELS = List.of(
            option("Low", "Baixa"),
            option("Medium", "Media"),
            option("High", "Alta")
    );

    private static final List<OptionResponse> LURE_CONDITIONS = List.of(
            option("New", "Nova"),
            option("Good", "Boa"),
            option("Used", "Usada"),
            option("Damaged", "Danificada"),
            option("Needs repair", "Precisa de reparacao")
    );

    private static final List<OptionResponse> FISH_STRIKE_ZONES = List.of(
            option("Surface", "Superficie"),
            option("Mid water", "Meia agua"),
            option("Bottom", "Fundo"),
            option("Structure", "Estruturas"),
            option("Cover", "Cobertura")
    );

    private static final List<OptionResponse> SESSION_STATUSES = List.of(
            option("planned", "Planeada"),
            option("active", "Ativa"),
            option("finished", "Terminada")
    );

    private static final List<OptionResponse> SESSION_EVENT_TYPES = List.of(
            option("OBSERVATION", "Observacao"),
            option("STRIKE", "Ataque"),
            option("CATCH", "Captura"),
            option("LURE_CHANGE", "Troca de lure"),
            option("WEATHER_CHANGE", "Mudanca de tempo"),
            option("SPOT_CHANGE", "Mudanca de spot"),
            option("NO_ACTIVITY", "Sem atividade"),
            option("NOTE", "Nota")
    );

    private static final List<OptionResponse> RECOMMENDATION_TYPES = List.of(
            option("PLAN", "Plano AI"),
            option("SESSION_ADJUSTMENT", "Ajuste durante sessao"),
            option("SESSION_REVIEW", "Review pos-sessao")
    );

    private static final List<OptionResponse> RECOMMENDATION_STEPS = List.of(
            option("PLAN_A", "Plano A"),
            option("PLAN_B", "Plano B"),
            option("PLAN_C", "Plano C"),
            option("SESSION_ADJUSTMENT", "Ajuste de sessao"),
            option("IMMEDIATE_ACTION", "Acao imediata"),
            option("NEXT_TECHNIQUE", "Proxima tecnica"),
            option("FALLBACK_ACTION", "Alternativa")
    );

    private static final List<OptionResponse> RECOMMENDATION_RESULTS = List.of(
            option("CATCH", "Captura"),
            option("STRIKE", "Ataque"),
            option("FOLLOW", "Seguido sem captura"),
            option("NO_ACTIVITY", "Sem atividade"),
            option("MISSED_FISH", "Peixe falhado"),
            option("BETTER_ACTION", "Melhorou a sessao"),
            option("WORSE_ACTION", "Piorou a sessao"),
            option("UNKNOWN", "Indefinido")
    );

    private static final List<OptionResponse> SORT_DIRECTIONS = List.of(
            option("asc", "Ascendente"),
            option("desc", "Descendente")
    );

    public FrontendOptionsResponse getAllOptions() {
        return new FrontendOptionsResponse(
                WATER_TYPES,
                WATER_CLARITIES,
                WATER_LEVELS,
                LURE_TYPES,
                LURE_DIFFICULTIES,
                LURE_EFFECTIVENESS_LEVELS,
                LURE_CONDITIONS,
                FISH_STRIKE_ZONES,
                SESSION_STATUSES,
                SESSION_EVENT_TYPES,
                RECOMMENDATION_TYPES,
                RECOMMENDATION_STEPS,
                RECOMMENDATION_RESULTS,
                SORT_DIRECTIONS
        );
    }

    public List<OptionResponse> getWaterTypes() {
        return WATER_TYPES;
    }

    public List<OptionResponse> getWaterClarities() {
        return WATER_CLARITIES;
    }

    public List<OptionResponse> getWaterLevels() {
        return WATER_LEVELS;
    }

    public List<OptionResponse> getLureTypes() {
        return LURE_TYPES;
    }

    public List<OptionResponse> getLureDifficulties() {
        return LURE_DIFFICULTIES;
    }

    public List<OptionResponse> getLureEffectivenessLevels() {
        return LURE_EFFECTIVENESS_LEVELS;
    }

    public List<OptionResponse> getLureConditions() {
        return LURE_CONDITIONS;
    }

    public List<OptionResponse> getFishStrikeZones() {
        return FISH_STRIKE_ZONES;
    }

    public List<OptionResponse> getSessionStatuses() {
        return SESSION_STATUSES;
    }

    public List<OptionResponse> getSessionEventTypes() {
        return SESSION_EVENT_TYPES;
    }

    public List<OptionResponse> getRecommendationTypes() {
        return RECOMMENDATION_TYPES;
    }

    public List<OptionResponse> getRecommendationSteps() {
        return RECOMMENDATION_STEPS;
    }

    public List<OptionResponse> getRecommendationResults() {
        return RECOMMENDATION_RESULTS;
    }

    public List<OptionResponse> getSortDirections() {
        return SORT_DIRECTIONS;
    }

    private static OptionResponse option(String value, String label) {
        return new OptionResponse(value, label);
    }
}
