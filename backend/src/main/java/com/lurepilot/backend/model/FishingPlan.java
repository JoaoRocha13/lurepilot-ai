package com.lurepilot.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "fishing_plans")
public class FishingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private FishingSpot spot;

    @Column(nullable = false)
    private LocalDate plannedDate;

    private LocalTime plannedTime;

    @Column(nullable = false)
    private String targetSpecies;

    @Column(nullable = false)
    private String waterClarity;

    @Column(nullable = false)
    private String waterLevel;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public FishingPlan() {
    }

    public FishingPlan(FishingSpot spot, LocalDate plannedDate, LocalTime plannedTime, String targetSpecies, String waterClarity, String waterLevel, String notes) {
        this.spot = spot;
        this.plannedDate = plannedDate;
        this.plannedTime = plannedTime;
        this.targetSpecies = targetSpecies;
        this.waterClarity = waterClarity;
        this.waterLevel = waterLevel;
        this.notes = notes;
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

    public FishingSpot getSpot() {
        return spot;
    }

    public void setSpot(FishingSpot spot) {
        this.spot = spot;
    }

    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public LocalTime getPlannedTime() {
        return plannedTime;
    }

    public void setPlannedTime(LocalTime plannedTime) {
        this.plannedTime = plannedTime;
    }

    public String getTargetSpecies() {
        return targetSpecies;
    }

    public void setTargetSpecies(String targetSpecies) {
        this.targetSpecies = targetSpecies;
    }

    public String getWaterClarity() {
        return waterClarity;
    }

    public void setWaterClarity(String waterClarity) {
        this.waterClarity = waterClarity;
    }

    public String getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(String waterLevel) {
        this.waterLevel = waterLevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
