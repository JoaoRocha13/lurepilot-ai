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
import java.time.LocalTime;

@Entity
@Table(name = "recommendation_executions", indexes = {
        @Index(name = "idx_rec_exec_recommendation", columnList = "recommendation_id"),
        @Index(name = "idx_rec_exec_plan", columnList = "plan_id"),
        @Index(name = "idx_rec_exec_session", columnList = "session_id"),
        @Index(name = "idx_rec_exec_step", columnList = "recommendation_step"),
        @Index(name = "idx_rec_exec_followed", columnList = "followed"),
        @Index(name = "idx_rec_exec_success", columnList = "success"),
        @Index(name = "idx_rec_exec_created_at", columnList = "created_at")
})
public class RecommendationExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recommendation_id", nullable = false)
    private AiRecommendation recommendation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private FishingPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private FishingSession session;

    @Column(nullable = false)
    private String recommendationType;

    private Integer recommendationVersion;

    @Column(nullable = false)
    private String recommendationStep;

    @Column(nullable = false)
    private Boolean followed;

    @Column(nullable = false)
    private String result;

    private Boolean success;

    private Integer rating;

    private LocalTime startedAt;

    private LocalTime endedAt;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public RecommendationExecution() {
    }

    public RecommendationExecution(
            AiRecommendation recommendation,
            FishingPlan plan,
            FishingSession session,
            String recommendationType,
            Integer recommendationVersion,
            String recommendationStep,
            Boolean followed,
            String result,
            Boolean success,
            Integer rating,
            LocalTime startedAt,
            LocalTime endedAt,
            String notes
    ) {
        this.recommendation = recommendation;
        this.plan = plan;
        this.session = session;
        this.recommendationType = recommendationType;
        this.recommendationVersion = recommendationVersion;
        this.recommendationStep = recommendationStep;
        this.followed = followed;
        this.result = result;
        this.success = success;
        this.rating = rating;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
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

    public AiRecommendation getRecommendation() {
        return recommendation;
    }

    public FishingPlan getPlan() {
        return plan;
    }

    public FishingSession getSession() {
        return session;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public Integer getRecommendationVersion() {
        return recommendationVersion;
    }

    public String getRecommendationStep() {
        return recommendationStep;
    }

    public Boolean getFollowed() {
        return followed;
    }

    public String getResult() {
        return result;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Integer getRating() {
        return rating;
    }

    public LocalTime getStartedAt() {
        return startedAt;
    }

    public LocalTime getEndedAt() {
        return endedAt;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
