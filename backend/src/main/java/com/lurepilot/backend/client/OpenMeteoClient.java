package com.lurepilot.backend.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

@Component
public class OpenMeteoClient {

    private final RestClient forecastClient;
    private final RestClient geocodingClient;

    public OpenMeteoClient(
            @Value("${lurepilot.weather.forecast-base-url}") String forecastBaseUrl,
            @Value("${lurepilot.weather.geocoding-base-url}") String geocodingBaseUrl,
            @Value("${lurepilot.weather.connect-timeout-seconds}") long connectTimeoutSeconds,
            @Value("${lurepilot.weather.read-timeout-seconds}") long readTimeoutSeconds
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));

        this.forecastClient = RestClient.builder()
                .baseUrl(forecastBaseUrl)
                .requestFactory(requestFactory)
                .build();
        this.geocodingClient = RestClient.builder()
                .baseUrl(geocodingBaseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public ForecastResponse getForecast(Double latitude, Double longitude) {
        ForecastResponse response = forecastClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("current", String.join(",",
                                "temperature_2m",
                                "relative_humidity_2m",
                                "apparent_temperature",
                                "precipitation",
                                "weather_code",
                                "cloud_cover",
                                "pressure_msl",
                                "wind_speed_10m",
                                "wind_direction_10m",
                                "wind_gusts_10m"
                        ))
                        .queryParam("hourly", String.join(",",
                                "temperature_2m",
                                "relative_humidity_2m",
                                "precipitation_probability",
                                "precipitation",
                                "weather_code",
                                "wind_speed_10m",
                                "wind_direction_10m",
                                "wind_gusts_10m"
                        ))
                        .queryParam("daily", String.join(",",
                                "temperature_2m_min",
                                "temperature_2m_max",
                                "precipitation_probability_max",
                                "precipitation_sum",
                                "weather_code",
                                "wind_direction_10m_dominant",
                                "wind_speed_10m_max",
                                "wind_gusts_10m_max",
                                "sunrise",
                                "sunset"
                        ))
                        .queryParam("timezone", "Europe/Lisbon")
                        .queryParam("forecast_days", 16)
                        .build())
                .retrieve()
                .body(ForecastResponse.class);

        if (response == null || response.daily() == null || response.daily().time() == null || response.daily().time().isEmpty()) {
            throw new IllegalStateException("Open-Meteo returned no forecast data");
        }

        return response;
    }

    public List<Location> searchLocations(String query, String countryCode) {
        GeocodingResponse response = geocodingClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/v1/search")
                            .queryParam("name", query)
                            .queryParam("count", 20)
                            .queryParam("language", "pt")
                            .queryParam("format", "json");
                    if (countryCode != null && !countryCode.isBlank()) {
                        builder.queryParam("countryCode", countryCode);
                    }
                    return builder.build();
                })
                .retrieve()
                .body(GeocodingResponse.class);

        return response == null || response.results() == null ? List.of() : response.results();
    }

    public record ForecastResponse(
            Double latitude,
            Double longitude,
            Double elevation,
            String timezone,
            CurrentWeather current,
            HourlyWeather hourly,
            DailyWeather daily
    ) {
    }

    public record CurrentWeather(
            String time,
            @JsonProperty("temperature_2m") Double temperature,
            @JsonProperty("relative_humidity_2m") Double relativeHumidity,
            @JsonProperty("apparent_temperature") Double apparentTemperature,
            Double precipitation,
            @JsonProperty("weather_code") Integer weatherCode,
            @JsonProperty("cloud_cover") Integer cloudCover,
            @JsonProperty("pressure_msl") Double pressureMsl,
            @JsonProperty("wind_speed_10m") Double windSpeed,
            @JsonProperty("wind_direction_10m") Integer windDirection,
            @JsonProperty("wind_gusts_10m") Double windGusts
    ) {
    }

    public record HourlyWeather(
            List<String> time,
            @JsonProperty("temperature_2m") List<Double> temperature,
            @JsonProperty("relative_humidity_2m") List<Double> relativeHumidity,
            @JsonProperty("precipitation_probability") List<Double> precipitationProbability,
            List<Double> precipitation,
            @JsonProperty("weather_code") List<Integer> weatherCode,
            @JsonProperty("wind_speed_10m") List<Double> windSpeed,
            @JsonProperty("wind_direction_10m") List<Integer> windDirection,
            @JsonProperty("wind_gusts_10m") List<Double> windGusts
    ) {
    }

    public record DailyWeather(
            List<String> time,
            @JsonProperty("temperature_2m_min") List<Double> temperatureMin,
            @JsonProperty("temperature_2m_max") List<Double> temperatureMax,
            @JsonProperty("precipitation_probability_max") List<Double> precipitationProbabilityMax,
            @JsonProperty("precipitation_sum") List<Double> precipitationSum,
            @JsonProperty("weather_code") List<Integer> weatherCode,
            @JsonProperty("wind_direction_10m_dominant") List<Integer> windDirection,
            @JsonProperty("wind_speed_10m_max") List<Double> windSpeedMax,
            @JsonProperty("wind_gusts_10m_max") List<Double> windGustsMax,
            List<String> sunrise,
            List<String> sunset
    ) {
    }

    public record GeocodingResponse(
            List<Location> results
    ) {
    }

    public record Location(
            Integer id,
            String name,
            Double latitude,
            Double longitude,
            Double elevation,
            @JsonProperty("feature_code") String featureCode,
            @JsonProperty("country_code") String countryCode,
            String country,
            String admin1,
            String admin2,
            String timezone
    ) {
    }
}
