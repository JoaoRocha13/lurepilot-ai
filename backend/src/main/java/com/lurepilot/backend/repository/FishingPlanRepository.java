package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FishingPlanRepository extends JpaRepository<FishingPlan, Long>, JpaSpecificationExecutor<FishingPlan> {

    List<FishingPlan> findTop5ByPlannedDateGreaterThanEqualOrderByPlannedDateAscPlannedTimeAscIdAsc(LocalDate plannedDate);

    Optional<FishingPlan> findFirstByPlannedDateGreaterThanEqualOrderByPlannedDateAscPlannedTimeAscIdAsc(LocalDate plannedDate);
}
