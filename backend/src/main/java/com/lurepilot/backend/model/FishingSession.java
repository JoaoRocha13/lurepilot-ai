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
@Table(name = "fishing_sessions")
public class FishingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private FishingSpot spot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private FishingPlan plan;

    @Column(name = "session_date", nullable = false)
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Column(nullable = false)
    private String targetSpecies;

    @Column(nullable = false)
    private String waterClarity;

    @Column(nullable = false)
    private String waterLevel;

    @Column(length = 1000)
    private String notes;

    private Boolean success;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public FishingSession() {
    }

    public FishingSession(FishingSpot spot, FishingPlan plan, LocalDate date, LocalTime startTime, LocalTime endTime, String targetSpecies, String waterClarity, String waterLevel, String notes, Boolean success) {
        this.spot = spot;
        this.plan = plan;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.targetSpecies = targetSpecies;
        this.waterClarity = waterClarity;
        this.waterLevel = waterLevel;
        this.notes = notes;
        this.success = success;
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

    public FishingPlan getPlan() {
        return plan;
    }

    public void setPlan(FishingPlan plan) {
        this.plan = plan;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
