package com.lurepilot.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "weather_snapshots", indexes = {
        @Index(name = "idx_weather_plan", columnList = "plan_id"),
        @Index(name = "idx_weather_session", columnList = "session_id"),
        @Index(name = "idx_weather_location", columnList = "source_location_id"),
        @Index(name = "idx_weather_forecast_date", columnList = "forecast_date"),
        @Index(name = "idx_weather_captured_at", columnList = "captured_at")
})
public class WeatherSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private FishingPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private FishingSession session;

    @Column(nullable = false)
    private String source;

    @Column(name = "source_location_id")
    private Integer sourceLocationId;

    private String sourceLocationName;

    private Double sourceLatitude;

    private Double sourceLongitude;

    private LocalDate forecastDate;

    private Instant dataUpdate;

    private Integer weatherTypeId;

    private Double temperatureMin;

    private Double temperatureMax;

    private Double precipitationProbability;

    private String windDirection;

    private Integer windSpeedClass;

    private Double currentTemperature;

    private Double apparentTemperature;

    private Double relativeHumidity;

    private Double precipitation;

    private Double pressureMsl;

    private Integer cloudCover;

    private Double windSpeedKmh;

    private Double windGustsKmh;

    private String sunrise;

    private String sunset;

    @Column(columnDefinition = "text")
    private String hourlyForecastJson;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant capturedAt;

    public WeatherSnapshot() {
    }

    public WeatherSnapshot(FishingPlan plan, FishingSession session, String source, Integer sourceLocationId, String sourceLocationName, Double sourceLatitude, Double sourceLongitude, LocalDate forecastDate, Instant dataUpdate, Integer weatherTypeId, Double temperatureMin, Double temperatureMax, Double precipitationProbability, String windDirection, Integer windSpeedClass, Double currentTemperature, Double apparentTemperature, Double relativeHumidity, Double precipitation, Double pressureMsl, Integer cloudCover, Double windSpeedKmh, Double windGustsKmh, String sunrise, String sunset, String hourlyForecastJson, String notes) {
        this.plan = plan;
        this.session = session;
        this.source = source;
        this.sourceLocationId = sourceLocationId;
        this.sourceLocationName = sourceLocationName;
        this.sourceLatitude = sourceLatitude;
        this.sourceLongitude = sourceLongitude;
        this.forecastDate = forecastDate;
        this.dataUpdate = dataUpdate;
        this.weatherTypeId = weatherTypeId;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.precipitationProbability = precipitationProbability;
        this.windDirection = windDirection;
        this.windSpeedClass = windSpeedClass;
        this.currentTemperature = currentTemperature;
        this.apparentTemperature = apparentTemperature;
        this.relativeHumidity = relativeHumidity;
        this.precipitation = precipitation;
        this.pressureMsl = pressureMsl;
        this.cloudCover = cloudCover;
        this.windSpeedKmh = windSpeedKmh;
        this.windGustsKmh = windGustsKmh;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.hourlyForecastJson = hourlyForecastJson;
        this.notes = notes;
    }

    @PrePersist
    public void prePersist() {
        if (capturedAt == null) {
            capturedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public FishingPlan getPlan() {
        return plan;
    }

    public FishingSession getSession() {
        return session;
    }

    public String getSource() {
        return source;
    }

    public Integer getSourceLocationId() {
        return sourceLocationId;
    }

    public String getSourceLocationName() {
        return sourceLocationName;
    }

    public Double getSourceLatitude() {
        return sourceLatitude;
    }

    public Double getSourceLongitude() {
        return sourceLongitude;
    }

    public LocalDate getForecastDate() {
        return forecastDate;
    }

    public Instant getDataUpdate() {
        return dataUpdate;
    }

    public Integer getWeatherTypeId() {
        return weatherTypeId;
    }

    public Double getTemperatureMin() {
        return temperatureMin;
    }

    public Double getTemperatureMax() {
        return temperatureMax;
    }

    public Double getPrecipitationProbability() {
        return precipitationProbability;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public Integer getWindSpeedClass() {
        return windSpeedClass;
    }

    public Double getCurrentTemperature() {
        return currentTemperature;
    }

    public Double getApparentTemperature() {
        return apparentTemperature;
    }

    public Double getRelativeHumidity() {
        return relativeHumidity;
    }

    public Double getPrecipitation() {
        return precipitation;
    }

    public Double getPressureMsl() {
        return pressureMsl;
    }

    public Integer getCloudCover() {
        return cloudCover;
    }

    public Double getWindSpeedKmh() {
        return windSpeedKmh;
    }

    public Double getWindGustsKmh() {
        return windGustsKmh;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getHourlyForecastJson() {
        return hourlyForecastJson;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }
}
