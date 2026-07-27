package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FishingSessionRepository extends JpaRepository<FishingSession, Long>, JpaSpecificationExecutor<FishingSession> {

    long countBySuccessTrue();

    List<FishingSession> findTop5ByOrderByDateDescIdDesc();

    List<FishingSession> findTop5ByStatusOrderByDateDescStartTimeDescIdDesc(FishingSessionStatus status);

    List<FishingSession> findTop5BySpotIdOrderByDateDescIdDesc(Long spotId);

    List<FishingSession> findTop5ByTargetSpeciesIgnoreCaseOrderByDateDescIdDesc(String targetSpecies);
}
