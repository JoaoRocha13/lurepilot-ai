package com.lurepilot.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "fishing_spots", indexes = {
        @Index(name = "idx_spots_name", columnList = "name"),
        @Index(name = "idx_spots_water_type", columnList = "water_type"),
        @Index(name = "idx_spots_spot_type", columnList = "spot_type"),
        @Index(name = "idx_spots_favorite_species", columnList = "favorite_species"),
        @Index(name = "idx_spots_created_at", columnList = "created_at")
})
public class FishingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String waterType;

    @Column(length = 50)
    private String spotType;

    private String favoriteSpecies;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public FishingSpot() {
    }

    public FishingSpot(String name, String description, Double latitude, Double longitude, String waterType, String favoriteSpecies) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.waterType = waterType;
        this.favoriteSpecies = favoriteSpecies;
    }

    public FishingSpot(String name, String description, Double latitude, Double longitude, String waterType, String spotType, String favoriteSpecies) {
        this(name, description, latitude, longitude, waterType, favoriteSpecies);
        this.spotType = spotType;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public String getSpotType() {
        return spotType;
    }

    public void setSpotType(String spotType) {
        this.spotType = spotType;
    }

    public String getFavoriteSpecies() {
        return favoriteSpecies;
    }

    public void setFavoriteSpecies(String favoriteSpecies) {
        this.favoriteSpecies = favoriteSpecies;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
