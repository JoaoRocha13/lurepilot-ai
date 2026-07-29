package com.lurepilot.backend.repository;

import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            select s.spot.id,
                   s.spot.name,
                   count(distinct s.id),
                   count(distinct case when s.success = true then s.id else null end),
                   coalesce(sum(c.quantity), 0),
                   max(s.date)
            from FishingSession s
            left join Catch c on c.session = s
            where (:dateFrom is null or s.date >= :dateFrom)
              and (:dateTo is null or s.date <= :dateTo)
              and (:species is null or lower(s.targetSpecies) = lower(:species))
              and (:spotId is null or s.spot.id = :spotId)
              and (:lureId is null or exists (
                  select sl.id from SessionLure sl
                  where sl.session = s
                    and sl.lure.id = :lureId
              ))
            group by s.spot.id, s.spot.name
            order by count(distinct case when s.success = true then s.id else null end) desc,
                     coalesce(sum(c.quantity), 0) desc,
                     count(distinct s.id) desc,
                     max(s.date) desc
            """)
    List<Object[]> findBestSpotInsights(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("species") String species,
            @Param("spotId") Long spotId,
            @Param("lureId") Long lureId,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("""
            select s.waterClarity,
                   s.waterLevel,
                   w.weatherTypeId,
                   w.windDirection,
                   w.windSpeedClass,
                   count(distinct s.id),
                   count(distinct case when s.success = true then s.id else null end),
                   coalesce(sum(c.quantity), 0),
                   avg(s.rating),
                   max(s.date)
            from FishingSession s
            left join WeatherSnapshot w on w.session = s
            left join Catch c on c.session = s
            where (:dateFrom is null or s.date >= :dateFrom)
              and (:dateTo is null or s.date <= :dateTo)
              and (:species is null or lower(s.targetSpecies) = lower(:species))
              and (:spotId is null or s.spot.id = :spotId)
              and (:lureId is null or exists (
                  select sl.id from SessionLure sl
                  where sl.session = s
                    and sl.lure.id = :lureId
              ))
            group by s.waterClarity, s.waterLevel, w.weatherTypeId, w.windDirection, w.windSpeedClass
            order by count(distinct case when s.success = true then s.id else null end) desc,
                     coalesce(sum(c.quantity), 0) desc,
                     count(distinct s.id) desc,
                     avg(s.rating) desc
            """)
    List<Object[]> findBestConditionInsights(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("species") String species,
            @Param("spotId") Long spotId,
            @Param("lureId") Long lureId,
            org.springframework.data.domain.Pageable pageable
    );
}
