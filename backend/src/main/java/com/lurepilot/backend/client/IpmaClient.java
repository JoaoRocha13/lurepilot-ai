package com.lurepilot.backend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Component
public class IpmaClient {

    private final RestClient restClient;

    public IpmaClient(
            @Value("${lurepilot.ipma.base-url}") String baseUrl,
            @Value("${lurepilot.ipma.connect-timeout-seconds}") long connectTimeoutSeconds,
            @Value("${lurepilot.ipma.read-timeout-seconds}") long readTimeoutSeconds
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public List<IpmaLocation> getLocations() {
        IpmaLocationsResponse response = restClient.get()
                .uri("/open-data/distrits-islands.json")
                .retrieve()
                .body(IpmaLocationsResponse.class);

        if (response == null || response.data() == null || response.data().isEmpty()) {
            throw new IllegalStateException("IPMA returned no locations");
        }

        return response.data();
    }

    public IpmaForecastResponse getDailyForecast(Integer globalIdLocal) {
        IpmaForecastResponse response = restClient.get()
                .uri("/open-data/forecast/meteorology/cities/daily/{globalIdLocal}.json", globalIdLocal)
                .retrieve()
                .body(IpmaForecastResponse.class);

        if (response == null || response.data() == null || response.data().isEmpty()) {
            throw new IllegalStateException("IPMA returned no forecast data");
        }

        return response;
    }

    public record IpmaLocationsResponse(
            List<IpmaLocation> data
    ) {
    }

    public record IpmaLocation(
            Integer globalIdLocal,
            String local,
            String latitude,
            String longitude
    ) {
    }

    public record IpmaForecastResponse(
            Integer globalIdLocal,
            String dataUpdate,
            List<IpmaForecastDay> data
    ) {
    }

    public record IpmaForecastDay(
            String precipitaProb,
            String probPrecipita,
            String tMin,
            String tMax,
            String predWindDir,
            Integer idWeatherType,
            Integer classWindSpeed,
            String longitude,
            String latitude,
            LocalDate forecastDate
    ) {
    }
}
