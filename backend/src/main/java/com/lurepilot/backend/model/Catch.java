package com.lurepilot.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "catches")
public class Catch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private FishingSession session;

    @Column(nullable = false)
    private String species;

    @Column(nullable = false)
    private Integer quantity;

    private Double sizeCm;

    private Double weightKg;

    private Boolean released;

    @Column(length = 1000)
    private String notes;

    public Catch() {
    }

    public Catch(FishingSession session, String species, Integer quantity, Double sizeCm, Double weightKg, Boolean released, String notes) {
        this.session = session;
        this.species = species;
        this.quantity = quantity;
        this.sizeCm = sizeCm;
        this.weightKg = weightKg;
        this.released = released;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public FishingSession getSession() {
        return session;
    }

    public void setSession(FishingSession session) {
        this.session = session;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSizeCm() {
        return sizeCm;
    }

    public void setSizeCm(Double sizeCm) {
        this.sizeCm = sizeCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Boolean getReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
