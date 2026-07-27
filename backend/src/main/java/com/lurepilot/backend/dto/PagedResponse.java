package com.lurepilot.backend.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> items,
        long totalItems,
        int page,
        int size,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
}
