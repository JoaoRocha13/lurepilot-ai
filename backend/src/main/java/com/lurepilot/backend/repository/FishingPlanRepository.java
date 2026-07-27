package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface FishingPlanRepository extends JpaRepository<FishingPlan, Long>, JpaSpecificationExecutor<FishingPlan> {

    List<FishingPlan> findTop5ByPlannedDateGreaterThanEqualOrderByPlannedDateAscPlannedTimeAscIdAsc(LocalDate plannedDate);
}
