package com.lurepilot.backend.service;

import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.FishingSpotRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SolunarServiceTest {

    @Test
    void calculatesSolunarForecastForSpot() {
        FishingSpotRepository repository = mock(FishingSpotRepository.class);
        FishingSpot spot = new FishingSpot("Alqueva", null, 38.2, -7.5, "Freshwater", "Reservoir", "Black Bass");
        when(repository.findById(1L)).thenReturn(Optional.of(spot));

        SolunarService service = new SolunarService(repository);

        assertNotNull(service.getForecast(1L, LocalDate.of(2026, 8, 6)));
    }
}
