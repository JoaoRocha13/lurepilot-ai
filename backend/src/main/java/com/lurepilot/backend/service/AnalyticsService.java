package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.AnalyticsCatchBucketResponse;
import com.lurepilot.backend.dto.AnalyticsLureUsageResponse;
import com.lurepilot.backend.dto.AnalyticsRecommendationStepResponse;
import com.lurepilot.backend.dto.AnalyticsSuccessBucketResponse;
import com.lurepilot.backend.dto.AnalyticsSummaryResponse;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.RecommendationExecutionRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {

    private static final int TOP_LIMIT = 5;

    private final FishingSessionRepository fishingSessionRepository;
    private final CatchRepository catchRepository;
    private final SessionLureRepository sessionLureRepository;
    private final RecommendationExecutionRepository recommendationExecutionRepository;

    public AnalyticsService(
            FishingSessionRepository fishingSessionRepository,
            CatchRepository catchRepository,
            SessionLureRepository sessionLureRepository,
            RecommendationExecutionRepository recommendationExecutionRepository
    ) {
        this.fishingSessionRepository = fishingSessionRepository;
        this.catchRepository = catchRepository;
        this.sessionLureRepository = sessionLureRepository;
        this.recommendationExecutionRepository = recommendationExecutionRepository;
    }

    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse getSummary() {
        long totalSessions = fishingSessionRepository.count();
        long finishedSessions = fishingSessionRepository.countByStatus(FishingSessionStatus.FINISHED);
        long successfulSessions = fishingSessionRepository.countBySuccessTrue();
        long totalRecommendationExecutions = recommendationExecutionRepository.count();
        long followedRecommendationExecutions = recommendationExecutionRepository.countByFollowedTrue();
        long successfulRecommendationExecutions = recommendationExecutionRepository.countBySuccessTrue();

        return new AnalyticsSummaryResponse(
                totalSessions,
                finishedSessions,
                successfulSessions,
                rate(successfulSessions, totalSessions),
                catchRepository.count(),
                catchRepository.sumTotalQuantity(),
                fishingSessionRepository.averageRating(),
                totalRecommendationExecutions,
                followedRecommendationExecutions,
                successfulRecommendationExecutions,
                rate(followedRecommendationExecutions, totalRecommendationExecutions),
                rate(successfulRecommendationExecutions, totalRecommendationExecutions),
                fishingSessionRepository.summarizeSuccessByTargetSpecies()
                        .stream()
                        .limit(TOP_LIMIT)
                        .map(this::toSuccessBucket)
                        .toList(),
                fishingSessionRepository.summarizeSuccessBySpot()
                        .stream()
                        .limit(TOP_LIMIT)
                        .map(this::toSuccessBucket)
                        .toList(),
                catchRepository.summarizeCatchQuantityBySpecies()
                        .stream()
                        .limit(TOP_LIMIT)
                        .map(this::toCatchBucket)
                        .toList(),
                sessionLureRepository.summarizeLureUsage()
                        .stream()
                        .limit(TOP_LIMIT)
                        .map(this::toLureUsage)
                        .toList(),
                recommendationExecutionRepository.summarizeByRecommendationStep()
                        .stream()
                        .map(this::toRecommendationStep)
                        .toList()
        );
    }

    private AnalyticsSuccessBucketResponse toSuccessBucket(Object[] row) {
        String label = asString(row[0]);
        long totalSessions = asLong(row[1]);
        long successfulSessions = asLong(row[2]);

        return new AnalyticsSuccessBucketResponse(
                label,
                totalSessions,
                successfulSessions,
                rate(successfulSessions, totalSessions)
        );
    }

    private AnalyticsCatchBucketResponse toCatchBucket(Object[] row) {
        return new AnalyticsCatchBucketResponse(
                asString(row[0]),
                asLong(row[1])
        );
    }

    private AnalyticsLureUsageResponse toLureUsage(Object[] row) {
        long timesUsed = asLong(row[1]);
        long successfulSessions = asLong(row[2]);

        return new AnalyticsLureUsageResponse(
                asString(row[0]),
                timesUsed,
                successfulSessions,
                rate(successfulSessions, timesUsed)
        );
    }

    private AnalyticsRecommendationStepResponse toRecommendationStep(Object[] row) {
        long totalExecutions = asLong(row[1]);
        long followedExecutions = asLong(row[2]);
        long successfulExecutions = asLong(row[3]);

        return new AnalyticsRecommendationStepResponse(
                asString(row[0]),
                totalExecutions,
                followedExecutions,
                successfulExecutions,
                rate(followedExecutions, totalExecutions),
                rate(successfulExecutions, totalExecutions)
        );
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

    private String asString(Object value) {
        return value == null ? "Unknown" : value.toString();
    }
}
