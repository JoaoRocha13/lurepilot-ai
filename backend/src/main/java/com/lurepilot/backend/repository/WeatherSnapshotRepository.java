package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.WeatherSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeatherSnapshotRepository extends JpaRepository<WeatherSnapshot, Long> {

    List<WeatherSnapshot> findByPlanIdOrderByCapturedAtDescIdDesc(Long planId);

    List<WeatherSnapshot> findBySessionIdOrderByCapturedAtDescIdDesc(Long sessionId);

    Optional<WeatherSnapshot> findFirstByPlanIdOrderByCapturedAtDescIdDesc(Long planId);

    Optional<WeatherSnapshot> findFirstBySessionIdOrderByCapturedAtDescIdDesc(Long sessionId);
}
