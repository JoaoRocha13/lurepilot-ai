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
@Table(name = "lure_library_items", indexes = {
        @Index(name = "idx_lure_library_name", columnList = "name"),
        @Index(name = "idx_lure_library_type", columnList = "type"),
        @Index(name = "idx_lure_library_difficulty", columnList = "difficulty"),
        @Index(name = "idx_lure_library_effectiveness", columnList = "effectiveness"),
        @Index(name = "idx_lure_library_created_at", columnList = "created_at")
})
public class LureLibraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String actionIconUrl;

    @Column(columnDefinition = "TEXT")
    private String actionImageUrl;

    private String difficulty;

    private String effectiveness;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String usageNotes;

    @Column(length = 500)
    private String actionType;

    @Column(length = 1000)
    private String idealConditions;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public LureLibraryItem() {
    }

    public LureLibraryItem(String name, String type, String imageUrl, String difficulty, String effectiveness, String description, String usageNotes, String actionType, String idealConditions) {
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;
        this.difficulty = difficulty;
        this.effectiveness = effectiveness;
        this.description = description;
        this.usageNotes = usageNotes;
        this.actionType = actionType;
        this.idealConditions = idealConditions;
    }

    public LureLibraryItem(String name, String type, String imageUrl, String difficulty, String effectiveness, String description, String usageNotes, String actionType, String idealConditions, String actionIconUrl, String actionImageUrl) {
        this(name, type, imageUrl, difficulty, effectiveness, description, usageNotes, actionType, idealConditions);
        this.actionIconUrl = actionIconUrl;
        this.actionImageUrl = actionImageUrl;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getActionIconUrl() {
        return actionIconUrl;
    }

    public void setActionIconUrl(String actionIconUrl) {
        this.actionIconUrl = actionIconUrl;
    }

    public String getActionImageUrl() {
        return actionImageUrl;
    }

    public void setActionImageUrl(String actionImageUrl) {
        this.actionImageUrl = actionImageUrl;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getEffectiveness() {
        return effectiveness;
    }

    public void setEffectiveness(String effectiveness) {
        this.effectiveness = effectiveness;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsageNotes() {
        return usageNotes;
    }

    public void setUsageNotes(String usageNotes) {
        this.usageNotes = usageNotes;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getIdealConditions() {
        return idealConditions;
    }

    public void setIdealConditions(String idealConditions) {
        this.idealConditions = idealConditions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
