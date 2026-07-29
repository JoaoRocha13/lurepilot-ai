package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.InsightBestConditionResponse;
import com.lurepilot.backend.dto.InsightBestSpotResponse;
import com.lurepilot.backend.dto.InsightRecommendationPerformanceResponse;
import com.lurepilot.backend.dto.InsightTopLureResponse;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.RecommendationExecutionRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;

@Service
public class InsightsService {

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 20;

    private final SessionLureRepository sessionLureRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final RecommendationExecutionRepository recommendationExecutionRepository;

    public InsightsService(
            SessionLureRepository sessionLureRepository,
            FishingSessionRepository fishingSessionRepository,
            RecommendationExecutionRepository recommendationExecutionRepository
    ) {
        this.sessionLureRepository = sessionLureRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.recommendationExecutionRepository = recommendationExecutionRepository;
    }

    @Transactional(readOnly = true)
    public List<InsightTopLureResponse> getTopLures(
            LocalDate dateFrom,
            LocalDate dateTo,
            String species,
            Long spotId,
            Long lureId,
            Integer limit
    ) {
        return sessionLureRepository.findTopLureInsights(
                        dateFrom,
                        dateTo,
                        normalize(species),
                        spotId,
                        lureId,
                        PageRequest.of(0, normalizeLimit(limit))
                )
                .stream()
                .map(this::toTopLure)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InsightBestSpotResponse> getBestSpots(
            LocalDate dateFrom,
            LocalDate dateTo,
            String species,
            Long spotId,
            Long lureId,
            Integer limit
    ) {
        return fishingSessionRepository.findBestSpotInsights(
                        dateFrom,
                        dateTo,
                        normalize(species),
                        spotId,
                        lureId,
                        PageRequest.of(0, normalizeLimit(limit))
                )
                .stream()
                .map(this::toBestSpot)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InsightBestConditionResponse> getBestConditions(
            LocalDate dateFrom,
            LocalDate dateTo,
            String species,
            Long spotId,
            Long lureId,
            Integer limit
    ) {
        return fishingSessionRepository.findBestConditionInsights(
                        dateFrom,
                        dateTo,
                        normalize(species),
                        spotId,
                        lureId,
                        PageRequest.of(0, normalizeLimit(limit))
                )
                .stream()
                .map(this::toBestCondition)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InsightRecommendationPerformanceResponse> getRecommendationPerformance(
            LocalDate dateFrom,
            LocalDate dateTo,
            String species,
            Long spotId,
            Long lureId,
            String recommendationType,
            Integer limit
    ) {
        return recommendationExecutionRepository.findRecommendationPerformanceInsights(
                        toStartInstant(dateFrom),
                        toExclusiveEndInstant(dateTo),
                        normalize(species),
                        spotId,
                        lureId,
                        normalizeRecommendationType(recommendationType),
                        PageRequest.of(0, normalizeLimit(limit))
                )
                .stream()
                .map(this::toRecommendationPerformance)
                .toList();
    }

    private InsightTopLureResponse toTopLure(Object[] row) {
        long timesUsed = asLong(row[3]);
        long successfulSessions = asLong(row[4]);

        return new InsightTopLureResponse(
                asLongObject(row[0]),
                asString(row[1]),
                asString(row[2]),
                timesUsed,
                successfulSessions,
                rate(successfulSessions, timesUsed),
                asLong(row[5]),
                (LocalDate) row[6]
        );
    }

    private InsightBestSpotResponse toBestSpot(Object[] row) {
        long totalSessions = asLong(row[2]);
        long successfulSessions = asLong(row[3]);

        return new InsightBestSpotResponse(
                asLongObject(row[0]),
                asString(row[1]),
                totalSessions,
                successfulSessions,
                rate(successfulSessions, totalSessions),
                asLong(row[4]),
                (LocalDate) row[5]
        );
    }

    private InsightBestConditionResponse toBestCondition(Object[] row) {
        long totalSessions = asLong(row[5]);
        long successfulSessions = asLong(row[6]);

        return new InsightBestConditionResponse(
                asString(row[0]),
                asString(row[1]),
                asInteger(row[2]),
                asString(row[3]),
                asInteger(row[4]),
                totalSessions,
                successfulSessions,
                rate(successfulSessions, totalSessions),
                asLong(row[7]),
                asDouble(row[8]),
                (LocalDate) row[9]
        );
    }

    private InsightRecommendationPerformanceResponse toRecommendationPerformance(Object[] row) {
        long totalExecutions = asLong(row[2]);
        long followedExecutions = asLong(row[3]);
        long successfulExecutions = asLong(row[4]);

        return new InsightRecommendationPerformanceResponse(
                asString(row[0]),
                asString(row[1]),
                totalExecutions,
                followedExecutions,
                successfulExecutions,
                rate(followedExecutions, totalExecutions),
                rate(successfulExecutions, totalExecutions),
                asDouble(row[5]),
                (Instant) row[6]
        );
    }

    private Instant toStartInstant(LocalDate date) {
        return date == null ? null : date.atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant toExclusiveEndInstant(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeRecommendationType(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }

        return Math.max(1, Math.min(MAX_LIMIT, limit));
    }

    private double rate(long value, long total) {
        if (total == 0) {
            return 0.0;
        }

        return Math.round(((double) value / total) * 1000.0) / 10.0;
    }

    private long asLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }

    private Long asLongObject(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }

    private Integer asInteger(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }

    private Double asDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : null;
    }

    private String asString(Object value) {
        return value == null ? "Unknown" : value.toString();
    }
}
