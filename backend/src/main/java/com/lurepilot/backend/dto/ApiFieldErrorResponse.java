package com.lurepilot.backend.dto;

public record ApiFieldErrorResponse(
        String field,
        String message,
        Object rejectedValue
) {
}
