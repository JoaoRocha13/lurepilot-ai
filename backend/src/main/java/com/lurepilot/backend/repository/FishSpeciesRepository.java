package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishSpecies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FishSpeciesRepository extends JpaRepository<FishSpecies, Long>, JpaSpecificationExecutor<FishSpecies> {
}
