package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FishingSessionRepository extends JpaRepository<FishingSession, Long> {

    long countBySuccessTrue();

    List<FishingSession> findTop5ByOrderByDateDescIdDesc();

    List<FishingSession> findTop5BySpotIdOrderByDateDescIdDesc(Long spotId);

    List<FishingSession> findTop5ByTargetSpeciesIgnoreCaseOrderByDateDescIdDesc(String targetSpecies);
}
