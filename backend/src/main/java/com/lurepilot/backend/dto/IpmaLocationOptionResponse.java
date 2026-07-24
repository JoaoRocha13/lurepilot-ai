package com.lurepilot.backend.dto;

public record IpmaLocationOptionResponse(
        Integer globalIdLocal,
        String name,
        Double latitude,
        Double longitude
) {
}
