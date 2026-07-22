package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FishingPlanRepository extends JpaRepository<FishingPlan, Long> {
}
