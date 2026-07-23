package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishSpecies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FishSpeciesRepository extends JpaRepository<FishSpecies, Long> {
}
