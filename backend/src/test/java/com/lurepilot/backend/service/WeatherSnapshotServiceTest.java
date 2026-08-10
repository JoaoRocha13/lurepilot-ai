package com.lurepilot.backend.service;

import com.lurepilot.backend.client.OpenMeteoClient;
import com.lurepilot.backend.dto.CreateWeatherCoordinateSnapshotRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private OpenMeteoClient openMeteoClient;

    @InjectMocks
    private WeatherSnapshotService weatherSnapshotService;

    @Test
    void createSnapshotForCoordinatesStoresDailyAndCurrentOpenMeteoData() {
        LocalDate forecastDate = LocalDate.of(2026, 8, 6);
        CreateWeatherCoordinateSnapshotRequest request = new CreateWeatherCoordinateSnapshotRequest(38.76, -9.13, forecastDate);

        when(openMeteoClient.getForecast(38.76, -9.13)).thenReturn(new OpenMeteoClient.ForecastResponse(
                38.76,
                -9.13,
                40.0,
                "Europe/Lisbon",
                new OpenMeteoClient.CurrentWeather(
                        "2026-08-06T12:00",
                        27.5,
                        52.0,
                        28.1,
                        0.0,
                        1,
                        20,
                        1015.0,
                        12.0,
                        315,
                        20.0
                ),
                new OpenMeteoClient.HourlyWeather(
                        List.of("2026-08-06T12:00"),
                        List.of(27.5),
                        List.of(52.0),
                        List.of(5.0),
                        List.of(0.0),
                        List.of(1),
                        List.of(12.0),
                        List.of(315),
                        List.of(20.0)
                ),
                new OpenMeteoClient.DailyWeather(
                        List.of(forecastDate.toString()),
                        List.of(18.0),
                        List.of(29.0),
                        List.of(10.0),
                        List.of(0.0),
                        List.of(1),
                        List.of(315),
                        List.of(18.0),
                        List.of(30.0),
                        List.of("2026-08-06T06:30"),
                        List.of("2026-08-06T20:45")
                )
        ));
        when(weatherSnapshotRepository.save(any(WeatherSnapshot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WeatherSnapshotResponse response = weatherSnapshotService.createSnapshotForCoordinates(request);

        assertThat(response.source()).isEqualTo("OPEN_METEO");
        assertThat(response.sourceLocationId()).isNull();
        assertThat(response.sourceLocationName()).isEqualTo("Coordenadas selecionadas");
        assertThat(response.forecastDate()).isEqualTo(forecastDate);
        assertThat(response.currentTemperature()).isEqualTo(27.5);
        assertThat(response.temperatureMin()).isEqualTo(18.0);
        assertThat(response.temperatureMax()).isEqualTo(29.0);
        assertThat(response.precipitationProbability()).isEqualTo(10.0);
        assertThat(response.windDirection()).isEqualTo("NW");
        assertThat(response.windSpeedKmh()).isEqualTo(12.0);
        assertThat(response.hourlyForecast()).hasSize(1);
    }

    @Test
    void createSnapshotForCoordinatesReturnsBadGatewayWhenOpenMeteoIsUnavailable() {
        when(openMeteoClient.getForecast(38.76, -9.13)).thenThrow(new IllegalStateException("Network unavailable"));

        assertThatThrownBy(() -> weatherSnapshotService.createSnapshotForCoordinates(
                new CreateWeatherCoordinateSnapshotRequest(38.76, -9.13, LocalDate.of(2026, 8, 6))
        ))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY)
                );
    }
}
