package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

final class ListQuerySupport {

    private static final int MAX_PAGE_SIZE = 100;

    private ListQuerySupport() {
    }

    static Pageable toPageable(int page, int size, String sortBy, String sortDirection, Map<String, String> allowedSortFields) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        String sortProperty = allowedSortFields.getOrDefault(normalizeSortKey(sortBy), allowedSortFields.get("id"));
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;

        return PageRequest.of(safePage, safeSize, Sort.by(direction, sortProperty));
    }

    static <T, R> PagedResponse<R> toPagedResponse(Page<T> page, Function<T, R> mapper) {
        List<R> items = page.getContent()
                .stream()
                .map(mapper)
                .toList();

        return new PagedResponse<>(
                items,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    static <T> PagedResponse<T> toPagedResponse(List<T> items, long totalItems, Pageable pageable) {
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / pageable.getPageSize());

        return new PagedResponse<>(
                items,
                totalItems,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                totalPages,
                pageable.getPageNumber() + 1 < totalPages,
                pageable.getPageNumber() > 0 && totalPages > 0
        );
    }

    static <T, R> PagedResponse<R> toPage(List<T> values, int page, int size, Function<T, R> mapper) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int totalItems = values.size();
        int fromIndex = Math.min(safePage * safeSize, totalItems);
        int toIndex = Math.min(fromIndex + safeSize, totalItems);
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / safeSize);

        List<R> items = values.subList(fromIndex, toIndex)
                .stream()
                .map(mapper)
                .toList();

        return new PagedResponse<>(
                items,
                totalItems,
                safePage,
                safeSize,
                totalPages,
                safePage + 1 < totalPages,
                safePage > 0 && totalPages > 0
        );
    }

    static <T, U extends Comparable<? super U>> Comparator<T> comparing(Function<T, U> extractor) {
        return Comparator.comparing(extractor, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    static <T> Comparator<T> applyDirection(Comparator<T> comparator, String sortDirection) {
        if ("desc".equalsIgnoreCase(sortDirection)) {
            return comparator.reversed();
        }

        return comparator;
    }

    private static String normalizeSortKey(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
