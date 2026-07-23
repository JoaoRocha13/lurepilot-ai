package com.lurepilot.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "fish_species")
public class FishSpecies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    @Column(length = 1000)
    private String habitatNotes;

    @Column(length = 1000)
    private String activeTimes;

    @Column(length = 500)
    private String strikeZone;

    @Column(length = 1000)
    private String commonZones;

    @Column(length = 1000)
    private String favoriteLures;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public FishSpecies() {
    }

    public FishSpecies(String name, String description, String imageUrl, String habitatNotes, String activeTimes, String strikeZone, String commonZones, String favoriteLures) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.habitatNotes = habitatNotes;
        this.activeTimes = activeTimes;
        this.strikeZone = strikeZone;
        this.commonZones = commonZones;
        this.favoriteLures = favoriteLures;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHabitatNotes() {
        return habitatNotes;
    }

    public void setHabitatNotes(String habitatNotes) {
        this.habitatNotes = habitatNotes;
    }

    public String getActiveTimes() {
        return activeTimes;
    }

    public void setActiveTimes(String activeTimes) {
        this.activeTimes = activeTimes;
    }

    public String getStrikeZone() {
        return strikeZone;
    }

    public void setStrikeZone(String strikeZone) {
        this.strikeZone = strikeZone;
    }

    public String getCommonZones() {
        return commonZones;
    }

    public void setCommonZones(String commonZones) {
        this.commonZones = commonZones;
    }

    public String getFavoriteLures() {
        return favoriteLures;
    }

    public void setFavoriteLures(String favoriteLures) {
        this.favoriteLures = favoriteLures;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
