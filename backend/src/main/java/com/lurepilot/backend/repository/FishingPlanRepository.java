package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FishingPlanRepository extends JpaRepository<FishingPlan, Long> {

    List<FishingPlan> findTop5ByPlannedDateGreaterThanEqualOrderByPlannedDateAscPlannedTimeAscIdAsc(LocalDate plannedDate);
}
