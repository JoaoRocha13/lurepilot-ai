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
        @Index(name = "idx_weather_location", columnList = "source_global_id_local"),
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

    private Integer sourceGlobalIdLocal;

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

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant capturedAt;

    public WeatherSnapshot() {
    }

    public WeatherSnapshot(FishingPlan plan, FishingSession session, String source, Integer sourceGlobalIdLocal, String sourceLocationName, Double sourceLatitude, Double sourceLongitude, LocalDate forecastDate, Instant dataUpdate, Integer weatherTypeId, Double temperatureMin, Double temperatureMax, Double precipitationProbability, String windDirection, Integer windSpeedClass, String notes) {
        this.plan = plan;
        this.session = session;
        this.source = source;
        this.sourceGlobalIdLocal = sourceGlobalIdLocal;
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

    public Integer getSourceGlobalIdLocal() {
        return sourceGlobalIdLocal;
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

    public String getNotes() {
        return notes;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }
}
