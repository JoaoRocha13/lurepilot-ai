package com.lurepilot.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "lures")
public class Lure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    private String color;

    private String size;

    private Double weight;

    private String brand;

    @Column(length = 1000)
    private String notes;

    private String targetSpecies;

    @Column(nullable = false)
    private String waterType;

    private Boolean active = true;

    private Integer quantity = 1;

    @Column(name = "lure_condition")
    private String condition;

    @Column(length = 1000)
    private String personalNotes;

    private String favoriteForSpecies;

    private String favoriteForSpot;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "library_item_id")
    private LureLibraryItem libraryItem;

    public Lure() {
    }

    public Lure(String name, String type, String color, String size, Double weight, String brand, String notes, String targetSpecies, String waterType) {
        this.name = name;
        this.type = type;
        this.color = color;
        this.size = size;
        this.weight = weight;
        this.brand = brand;
        this.notes = notes;
        this.targetSpecies = targetSpecies;
        this.waterType = waterType;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (active == null) {
            active = true;
        }
        if (quantity == null) {
            quantity = 1;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTargetSpecies() {
        return targetSpecies;
    }

    public void setTargetSpecies(String targetSpecies) {
        this.targetSpecies = targetSpecies;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPersonalNotes() {
        return personalNotes;
    }

    public void setPersonalNotes(String personalNotes) {
        this.personalNotes = personalNotes;
    }

    public String getFavoriteForSpecies() {
        return favoriteForSpecies;
    }

    public void setFavoriteForSpecies(String favoriteForSpecies) {
        this.favoriteForSpecies = favoriteForSpecies;
    }

    public String getFavoriteForSpot() {
        return favoriteForSpot;
    }

    public void setFavoriteForSpot(String favoriteForSpot) {
        this.favoriteForSpot = favoriteForSpot;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public LureLibraryItem getLibraryItem() {
        return libraryItem;
    }

    public void setLibraryItem(LureLibraryItem libraryItem) {
        this.libraryItem = libraryItem;
    }
}
