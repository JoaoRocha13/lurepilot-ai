package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.Lure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LureRepository extends JpaRepository<Lure, Long>, JpaSpecificationExecutor<Lure> {
}
