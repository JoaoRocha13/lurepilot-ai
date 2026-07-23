package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.LureLibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LureLibraryItemRepository extends JpaRepository<LureLibraryItem, Long> {
}
