package com.lurepilot.backend.service;

import com.lurepilot.backend.client.IpmaClient;
import com.lurepilot.backend.dto.CreateIpmaCoordinateSnapshotRequest;
import com.lurepilot.backend.dto.WeatherSnapshotResponse;
import com.lurepilot.backend.model.WeatherSnapshot;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.WeatherSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherSnapshotServiceTest {

    @Mock
    private WeatherSnapshotRepository weatherSnapshotRepository;

    @Mock
    private FishingPlanRepository fishingPlanRepository;

    @Mock
    private FishingSessionRepository fishingSessionRepository;

    @Mock
    private IpmaClient ipmaClient;

    @InjectMocks
    private WeatherSnapshotService weatherSnapshotService;

    @Test
    void createIpmaSnapshotForCoordinatesUsesNearestIpmaLocation() {
        LocalDate forecastDate = LocalDate.of(2026, 7, 24);
        CreateIpmaCoordinateSnapshotRequest request = new CreateIpmaCoordinateSnapshotRequest(38.76, -9.13, forecastDate);

        when(ipmaClient.getLocations()).thenReturn(List.of(
                new IpmaClient.IpmaLocation(1110600, "Lisboa", "38.766", "-9.1286"),
                new IpmaClient.IpmaLocation(1080500, "Faro", "37.0144", "-7.9659")
        ));
        when(ipmaClient.getDailyForecast(1110600)).thenReturn(new IpmaClient.IpmaForecastResponse(
                1110600,
                "2026-07-24T13:31:02Z",
                List.of(new IpmaClient.IpmaForecastDay(
                        "0",
                        null,
                        "19.2",
                        "26.7",
                        "NW",
                        2,
                        2,
                        "-9.1286",
                        "38.766",
                        forecastDate
                ))
        ));
        when(weatherSnapshotRepository.save(any(WeatherSnapshot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WeatherSnapshotResponse response = weatherSnapshotService.createIpmaSnapshotForCoordinates(request);

        assertThat(response.source()).isEqualTo("IPMA");
        assertThat(response.sourceGlobalIdLocal()).isEqualTo(1110600);
        assertThat(response.sourceLocationName()).isEqualTo("Lisboa");
        assertThat(response.forecastDate()).isEqualTo(forecastDate);
        assertThat(response.temperatureMin()).isEqualTo(19.2);
        assertThat(response.temperatureMax()).isEqualTo(26.7);
        assertThat(response.precipitationProbability()).isZero();
        assertThat(response.notes()).contains("Custom coordinates");
    }
}
