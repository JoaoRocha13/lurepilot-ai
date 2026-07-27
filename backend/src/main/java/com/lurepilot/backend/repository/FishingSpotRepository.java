package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FishingSpotRepository extends JpaRepository<FishingSpot, Long>, JpaSpecificationExecutor<FishingSpot> {
}
