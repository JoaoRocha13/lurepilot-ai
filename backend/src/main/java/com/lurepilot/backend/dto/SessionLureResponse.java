package com.lurepilot.backend.dto;

import java.time.LocalTime;

public record SessionLureResponse(
        Long id,
        Long sessionId,
        Long lureId,
        String lureName,
        String lureType,
        LocalTime usedFrom,
        LocalTime usedTo,
        String resultNotes
) {
}
