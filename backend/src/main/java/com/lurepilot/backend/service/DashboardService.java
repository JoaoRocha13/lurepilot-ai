package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.DashboardRecentSessionResponse;
import com.lurepilot.backend.dto.DashboardResponse;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.FishSpeciesRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.FishingSpotRepository;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import com.lurepilot.backend.repository.LureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class DashboardService {

    private final FishingSpotRepository fishingSpotRepository;
    private final FishSpeciesRepository fishSpeciesRepository;
    private final LureRepository lureRepository;
    private final LureLibraryItemRepository lureLibraryItemRepository;
    private final FishingPlanRepository fishingPlanRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final CatchRepository catchRepository;

    public DashboardService(
            FishingSpotRepository fishingSpotRepository,
            FishSpeciesRepository fishSpeciesRepository,
            LureRepository lureRepository,
            LureLibraryItemRepository lureLibraryItemRepository,
            FishingPlanRepository fishingPlanRepository,
            FishingSessionRepository fishingSessionRepository,
            CatchRepository catchRepository
    ) {
        this.fishingSpotRepository = fishingSpotRepository;
        this.fishSpeciesRepository = fishSpeciesRepository;
        this.lureRepository = lureRepository;
        this.lureLibraryItemRepository = lureLibraryItemRepository;
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.catchRepository = catchRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        List<DashboardRecentSessionResponse> recentSessions = fishingSessionRepository.findTop5ByOrderByDateDescIdDesc()
                .stream()
                .map(this::toRecentSessionResponse)
                .toList();

        return new DashboardResponse(
                fishingSpotRepository.count(),
                fishSpeciesRepository.count(),
                lureRepository.count(),
                lureLibraryItemRepository.count(),
                fishingPlanRepository.count(),
                fishingSessionRepository.count(),
                fishingSessionRepository.countBySuccessTrue(),
                catchRepository.count(),
                catchRepository.sumTotalQuantity(),
                recentSessions
        );
    }

    private DashboardRecentSessionResponse toRecentSessionResponse(FishingSession session) {
        FishingSpot spot = session.getSpot();

        return new DashboardRecentSessionResponse(
                session.getId(),
                spot.getId(),
                spot.getName(),
                session.getDate(),
                session.getStartTime(),
                statusOrDefault(session).name().toLowerCase(Locale.ROOT),
                session.getTargetSpecies(),
                session.getSuccess()
        );
    }

    private FishingSessionStatus statusOrDefault(FishingSession session) {
        if (session.getStatus() != null) {
            return session.getStatus();
        }

        if (session.getEndTime() != null || session.getSuccess() != null) {
            return FishingSessionStatus.FINISHED;
        }

        if (session.getStartTime() != null) {
            return FishingSessionStatus.ACTIVE;
        }

        return FishingSessionStatus.PLANNED;
    }
}
