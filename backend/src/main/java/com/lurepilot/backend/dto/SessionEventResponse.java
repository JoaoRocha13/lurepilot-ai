package com.lurepilot.backend.dto;

import java.time.LocalTime;

public record SessionEventResponse(
        Long id,
        Long sessionId,
        LocalTime eventTime,
        String eventType,
        String description
) {
}
