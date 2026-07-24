package com.lurepilot.backend.service;

import com.lurepilot.backend.client.IpmaClient;
import com.lurepilot.backend.dto.IpmaLocationOptionResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class IpmaLocationService {

    private final IpmaClient ipmaClient;

    public IpmaLocationService(IpmaClient ipmaClient) {
        this.ipmaClient = ipmaClient;
    }

    public List<IpmaLocationOptionResponse> getLocations() {
        return ipmaClient.getLocations()
                .stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing(IpmaLocationOptionResponse::name))
                .toList();
    }

    public List<IpmaLocationOptionResponse> searchLocations(String query) {
        String normalizedQuery = normalize(query);

        if (normalizedQuery.isBlank()) {
            return getLocations();
        }

        return getLocations()
                .stream()
                .filter(location -> normalize(location.name()).contains(normalizedQuery))
                .toList();
    }

    private IpmaLocationOptionResponse toResponse(IpmaClient.IpmaLocation location) {
        return new IpmaLocationOptionResponse(
                location.globalIdLocal(),
                location.local(),
                parseDouble(location.latitude()),
                parseDouble(location.longitude())
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Double.parseDouble(value);
    }
}
