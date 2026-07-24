package com.lurepilot.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ai_recommendations")
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private FishingPlan plan;

    @Lob
    @Column(nullable = false)
    private String contextJson;

    @Lob
    @Column(nullable = false)
    private String rawResponse;

    @Column(length = 1000)
    private String summary;

    @Lob
    private String lureRankingJson;

    @Column(length = 1000)
    private String planA;

    @Column(length = 1000)
    private String planB;

    @Column(length = 1000)
    private String planC;

    @Lob
    private String avoidJson;

    private String confidence;

    @Lob
    private String warningsJson;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public AiRecommendation() {
    }

    public AiRecommendation(FishingPlan plan, String contextJson, String rawResponse, String summary, String lureRankingJson, String planA, String planB, String planC, String avoidJson, String confidence, String warningsJson) {
        this.plan = plan;
        this.contextJson = contextJson;
        this.rawResponse = rawResponse;
        this.summary = summary;
        this.lureRankingJson = lureRankingJson;
        this.planA = planA;
        this.planB = planB;
        this.planC = planC;
        this.avoidJson = avoidJson;
        this.confidence = confidence;
        this.warningsJson = warningsJson;
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

    public FishingPlan getPlan() {
        return plan;
    }

    public String getContextJson() {
        return contextJson;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public String getSummary() {
        return summary;
    }

    public String getLureRankingJson() {
        return lureRankingJson;
    }

    public String getPlanA() {
        return planA;
    }

    public String getPlanB() {
        return planB;
    }

    public String getPlanC() {
        return planC;
    }

    public String getAvoidJson() {
        return avoidJson;
    }

    public String getConfidence() {
        return confidence;
    }

    public String getWarningsJson() {
        return warningsJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
