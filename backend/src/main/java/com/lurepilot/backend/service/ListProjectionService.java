package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.FishSpeciesSummaryResponse;
import com.lurepilot.backend.dto.CatchGalleryItemResponse;
import com.lurepilot.backend.dto.FishingPlanSummaryResponse;
import com.lurepilot.backend.dto.FishingSessionSummaryResponse;
import com.lurepilot.backend.dto.FishingSpotSummaryResponse;
import com.lurepilot.backend.dto.LureBoxItemSummaryResponse;
import com.lurepilot.backend.dto.LureLibraryItemSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.model.Catch;
import com.lurepilot.backend.model.FishSpecies;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.LureLibraryItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListProjectionService {

    @PersistenceContext
    private EntityManager entityManager;

    public PagedResponse<FishingSpotSummaryResponse> findFishingSpotSummaries(Specification<FishingSpot> specification, Pageable pageable) {
        return findProjectedPage(
                FishingSpot.class,
                FishingSpotSummaryResponse.class,
                specification,
                pageable,
                (root, criteriaBuilder) -> criteriaBuilder.construct(
                        FishingSpotSummaryResponse.class,
                        root.get("id"),
                        root.get("name"),
                        root.get("latitude"),
                        root.get("longitude"),
                        root.get("waterType"),
                        root.get("favoriteSpecies")
                )
        );
    }

    public PagedResponse<FishSpeciesSummaryResponse> findFishSpeciesSummaries(Specification<FishSpecies> specification, Pageable pageable) {
        return findProjectedPage(
                FishSpecies.class,
                FishSpeciesSummaryResponse.class,
                specification,
                pageable,
                (root, criteriaBuilder) -> criteriaBuilder.construct(
                        FishSpeciesSummaryResponse.class,
                        root.get("id"),
                        root.get("name"),
                        root.get("imageUrl"),
                        root.get("strikeZone"),
                        root.get("favoriteLures")
                )
        );
    }

    public PagedResponse<LureLibraryItemSummaryResponse> findLureLibraryItemSummaries(Specification<LureLibraryItem> specification, Pageable pageable) {
        return findProjectedPage(
                LureLibraryItem.class,
                LureLibraryItemSummaryResponse.class,
                specification,
                pageable,
                (root, criteriaBuilder) -> criteriaBuilder.construct(
                        LureLibraryItemSummaryResponse.class,
                        root.get("id"),
                        root.get("name"),
                        root.get("type"),
                        root.get("imageUrl"),
                        root.get("difficulty"),
                        root.get("effectiveness"),
                        root.get("actionType")
                )
        );
    }

    public PagedResponse<LureBoxItemSummaryResponse> findLureBoxSummaries(Specification<Lure> specification, Pageable pageable) {
        return findProjectedPage(
                Lure.class,
                LureBoxItemSummaryResponse.class,
                specification,
                pageable,
                (root, criteriaBuilder) -> {
                    Join<Lure, LureLibraryItem> libraryItem = root.join("libraryItem", JoinType.LEFT);

                    return criteriaBuilder.construct(
                            LureBoxItemSummaryResponse.class,
                            root.get("id"),
                            root.get("name"),
                            root.get("type"),
                            root.get("color"),
                            root.get("size"),
                            root.get("brand"),
                            libraryItem.get("id"),
                            libraryItem.get("name"),
                            root.get("targetSpecies"),
                            root.get("waterType"),
                            coalesce(criteriaBuilder, root.get("active"), true),
                            coalesce(criteriaBuilder, root.get("quantity"), 1),
                            root.get("condition")
                    );
                }
        );
    }

    public PagedResponse<FishingPlanSummaryResponse> findFishingPlanSummaries(Specification<FishingPlan> specification, Pageable pageable) {
        return findProjectedPage(
                FishingPlan.class,
                FishingPlanSummaryResponse.class,
                specification,
                pageable,
                (root, criteriaBuilder) -> {
                    Join<FishingPlan, FishingSpot> spot = root.join("spot", JoinType.LEFT);

                    return criteriaBuilder.construct(
                            FishingPlanSummaryResponse.class,
                            root.get("id"),
                            spot.get("id"),
                            spot.get("name"),
                            root.get("plannedDate"),
                            root.get("plannedTime"),
                            root.get("targetSpecies"),
                            root.get("waterClarity"),
                            root.get("waterLevel")
                    );
                }
        );
    }

    public PagedResponse<FishingSessionSummaryResponse> findFishingSessionSummaries(Specification<FishingSession> specification, Pageable pageable) {
        return findProjectedPage(
                FishingSession.class,
                FishingSessionSummaryResponse.class,
                specification,
                pageable,
                (root, criteriaBuilder) -> {
                    Join<FishingSession, FishingSpot> spot = root.join("spot", JoinType.LEFT);
                    Join<FishingSession, FishingPlan> plan = root.join("plan", JoinType.LEFT);

                    return criteriaBuilder.construct(
                            FishingSessionSummaryResponse.class,
                            root.get("id"),
                            spot.get("id"),
                            spot.get("name"),
                            plan.get("id"),
                            root.get("date"),
                            root.get("startTime"),
                            root.get("endTime"),
                            sessionStatusExpression(root, criteriaBuilder),
                            root.get("targetSpecies"),
                            root.get("success"),
                            root.get("rating")
                    );
                }
        );
    }

    public PagedResponse<CatchGalleryItemResponse> findCatchGalleryItems(Specification<Catch> specification, Pageable pageable) {
        return findProjectedPage(
                Catch.class,
                CatchGalleryItemResponse.class,
                specification,
                pageable,
                (root, criteriaBuilder) -> {
                    Join<Catch, FishingSession> session = root.join("session", JoinType.LEFT);
                    Join<FishingSession, FishingSpot> spot = session.join("spot", JoinType.LEFT);

                    return criteriaBuilder.construct(
                            CatchGalleryItemResponse.class,
                            root.get("id"),
                            session.get("id"),
                            session.get("date"),
                            session.get("startTime"),
                            spot.get("id"),
                            spot.get("name"),
                            root.get("species"),
                            root.get("quantity"),
                            root.get("sizeCm"),
                            root.get("weightKg"),
                            root.get("released"),
                            root.get("photoUrl"),
                            root.get("photoThumbnailUrl"),
                            root.get("photoCaption"),
                            session.get("success"),
                            session.get("rating")
                    );
                }
        );
    }

    private <E, D> PagedResponse<D> findProjectedPage(
            Class<E> entityClass,
            Class<D> dtoClass,
            Specification<E> specification,
            Pageable pageable,
            ProjectionBuilder<E, D> projectionBuilder
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<D> query = criteriaBuilder.createQuery(dtoClass);
        Root<E> root = query.from(entityClass);
        query.select(projectionBuilder.build(root, criteriaBuilder));
        applySpecification(query, root, criteriaBuilder, specification);
        applyOrder(query, root, criteriaBuilder, pageable);

        List<D> items = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        long totalItems = count(entityClass, specification);

        return ListQuerySupport.toPagedResponse(items, totalItems, pageable);
    }

    private <E> long count(Class<E> entityClass, Specification<E> specification) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<E> root = query.from(entityClass);
        query.select(criteriaBuilder.count(root));
        applySpecification(query, root, criteriaBuilder, specification);

        return entityManager.createQuery(query).getSingleResult();
    }

    private <E, D> void applySpecification(
            CriteriaQuery<D> query,
            Root<E> root,
            CriteriaBuilder criteriaBuilder,
            Specification<E> specification
    ) {
        if (specification == null) {
            return;
        }

        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
        if (predicate != null) {
            query.where(predicate);
        }
    }

    private <E, D> void applyOrder(CriteriaQuery<D> query, Root<E> root, CriteriaBuilder criteriaBuilder, Pageable pageable) {
        List<Order> orders = new ArrayList<>();
        for (Sort.Order sortOrder : pageable.getSort()) {
            Expression<?> expression = path(root, sortOrder.getProperty());
            orders.add(sortOrder.isAscending() ? criteriaBuilder.asc(expression) : criteriaBuilder.desc(expression));
        }

        if (!orders.isEmpty()) {
            query.orderBy(orders);
        }
    }

    private <T> Expression<T> coalesce(CriteriaBuilder criteriaBuilder, Expression<T> expression, T fallback) {
        CriteriaBuilder.Coalesce<T> coalesce = criteriaBuilder.coalesce();
        coalesce.value(expression);
        coalesce.value(fallback);
        return coalesce;
    }

    private Expression<String> sessionStatusExpression(Root<FishingSession> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.<String>selectCase()
                .when(root.get("status").isNotNull(), criteriaBuilder.lower(root.get("status").as(String.class)))
                .when(criteriaBuilder.or(root.get("endTime").isNotNull(), root.get("success").isNotNull()), "finished")
                .when(root.get("startTime").isNotNull(), "active")
                .otherwise("planned");
    }

    @SuppressWarnings("unchecked")
    private <Y> Expression<Y> path(Root<?> root, String propertyPath) {
        String[] properties = propertyPath.split("\\.");
        if (properties.length == 1) {
            return root.get(properties[0]);
        }

        From<?, ?> from = root;
        for (int index = 0; index < properties.length - 1; index++) {
            from = from.join(properties[index], JoinType.LEFT);
        }

        return (Expression<Y>) from.get(properties[properties.length - 1]);
    }

    @FunctionalInterface
    private interface ProjectionBuilder<E, D> {
        Selection<D> build(Root<E> root, CriteriaBuilder criteriaBuilder);
    }
}
