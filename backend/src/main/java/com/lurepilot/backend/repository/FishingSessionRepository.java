package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FishingSessionRepository extends JpaRepository<FishingSession, Long>, JpaSpecificationExecutor<FishingSession> {

    long countBySuccessTrue();

    long countByStatus(FishingSessionStatus status);

    List<FishingSession> findTop5ByOrderByDateDescIdDesc();

    List<FishingSession> findTop5ByStatusOrderByDateDescStartTimeDescIdDesc(FishingSessionStatus status);

    Optional<FishingSession> findFirstByStatusAndDateGreaterThanEqualOrderByDateAscStartTimeAscIdAsc(FishingSessionStatus status, LocalDate date);

    List<FishingSession> findTop5BySpotIdOrderByDateDescIdDesc(Long spotId);

    List<FishingSession> findTop5ByTargetSpeciesIgnoreCaseOrderByDateDescIdDesc(String targetSpecies);

    @Query("select avg(s.rating) from FishingSession s where s.rating is not null")
    Double averageRating();

    @Query("""
            select s.targetSpecies, count(s), sum(case when s.success = true then 1 else 0 end)
            from FishingSession s
            group by s.targetSpecies
            order by count(s) desc
            """)
    List<Object[]> summarizeSuccessByTargetSpecies();

    @Query("""
            select s.spot.name, count(s), sum(case when s.success = true then 1 else 0 end)
            from FishingSession s
            group by s.spot.id, s.spot.name
            order by count(s) desc
            """)
    List<Object[]> summarizeSuccessBySpot();
}
