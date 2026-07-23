package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishingPlanLure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FishingPlanLureRepository extends JpaRepository<FishingPlanLure, Long> {

    List<FishingPlanLure> findByPlanIdOrderByIdAsc(Long planId);

    boolean existsByPlanIdAndLureId(Long planId, Long lureId);
}
