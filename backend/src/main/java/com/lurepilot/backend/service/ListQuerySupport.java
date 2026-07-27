package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.PagedResponse;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

final class ListQuerySupport {

    private static final int MAX_PAGE_SIZE = 100;

    private ListQuerySupport() {
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
}
