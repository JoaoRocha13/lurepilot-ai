package com.lurepilot.backend.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;

final class SearchSpecifications {

    private SearchSpecifications() {
    }

    static <T> Specification<T> containsAny(String query, String... propertyPaths) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (isBlank(query)) {
                return criteriaBuilder.conjunction();
            }

            String pattern = containsPattern(query);
            Predicate[] predicates = Arrays.stream(propertyPaths)
                    .map(propertyPath -> criteriaBuilder.like(
                            criteriaBuilder.lower(stringExpression(root, propertyPath, criteriaBuilder)),
                            pattern
                    ))
                    .toArray(Predicate[]::new);

            return criteriaBuilder.or(predicates);
        };
    }

    static <T> Specification<T> contains(String value, String propertyPath) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (isBlank(value)) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(stringExpression(root, propertyPath, criteriaBuilder)),
                    containsPattern(value)
            );
        };
    }

    static <T> Specification<T> equalsIgnoreCase(String value, String propertyPath) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (isBlank(value)) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(stringExpression(root, propertyPath, criteriaBuilder)),
                    normalize(value)
            );
        };
    }

    static <T> Specification<T> equalsValue(Object value, String propertyPath) {
        return (root, criteriaQuery, criteriaBuilder) -> value == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(path(root, propertyPath), value);
    }

    static <T> Specification<T> isActive(Boolean active) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (active == null) {
                return criteriaBuilder.conjunction();
            }

            Predicate exactActive = criteriaBuilder.equal(root.get("active"), active);
            if (active) {
                return criteriaBuilder.or(exactActive, root.get("active").isNull());
            }

            return exactActive;
        };
    }

    static <T> Specification<T> quantityAtLeast(Integer minQuantity) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (minQuantity == null) {
                return criteriaBuilder.conjunction();
            }

            Predicate quantityMatches = criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), minQuantity);
            if (minQuantity <= 1) {
                return criteriaBuilder.or(quantityMatches, root.get("quantity").isNull());
            }

            return quantityMatches;
        };
    }

    static <T> Specification<T> dateFrom(LocalDate value, String propertyPath) {
        return (root, criteriaQuery, criteriaBuilder) -> value == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.greaterThanOrEqualTo(path(root, propertyPath), value);
    }

    static <T> Specification<T> dateTo(LocalDate value, String propertyPath) {
        return (root, criteriaQuery, criteriaBuilder) -> value == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.lessThanOrEqualTo(path(root, propertyPath), value);
    }

    private static Expression<String> stringExpression(Root<?> root, String propertyPath, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.coalesce(path(root, propertyPath).as(String.class), "");
    }

    @SuppressWarnings("unchecked")
    private static <Y> Path<Y> path(Root<?> root, String propertyPath) {
        String[] properties = propertyPath.split("\\.");
        if (properties.length == 1) {
            return root.get(properties[0]);
        }

        From<?, ?> from = root;
        for (int index = 0; index < properties.length - 1; index++) {
            from = from.join(properties[index], JoinType.LEFT);
        }

        return (Path<Y>) from.get(properties[properties.length - 1]);
    }

    private static String containsPattern(String value) {
        return "%" + normalize(value)
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_") + "%";
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase();
    }
}
