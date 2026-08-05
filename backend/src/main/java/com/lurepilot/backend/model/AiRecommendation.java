package com.lurepilot.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ai_recommendations", indexes = {
        @Index(name = "idx_ai_rec_plan_type_latest", columnList = "plan_id, recommendation_type, latest"),
        @Index(name = "idx_ai_rec_session_type_latest", columnList = "session_id, recommendation_type, latest"),
        @Index(name = "idx_ai_rec_plan_type_version", columnList = "plan_id, recommendation_type, version"),
        @Index(name = "idx_ai_rec_session_type_version", columnList = "session_id, recommendation_type, version"),
        @Index(name = "idx_ai_rec_created_at", columnList = "created_at")
})
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private FishingPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private FishingSession session;

    private String recommendationType;

    private Integer version;

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

    private Integer confidenceScore;

    @Column(length = 1000)
    private String confidenceReason;

    private Boolean latest = true;

    private Boolean saved = false;

    private Instant supersededAt;

    @Lob
    private String warningsJson;

    @Lob
    private String extraJson;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public AiRecommendation() {
    }

    public AiRecommendation(FishingPlan plan, FishingSession session, String recommendationType, Integer version, String contextJson, String rawResponse, String summary, String lureRankingJson, String planA, String planB, String planC, String avoidJson, String confidence, String warningsJson) {
        this.plan = plan;
        this.session = session;
        this.recommendationType = recommendationType;
        this.version = version;
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
        if (latest == null) {
            latest = true;
        }
        if (saved == null) {
            saved = false;
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

    public String getRecommendationType() {
        return recommendationType;
    }

    public Integer getVersion() {
        return version;
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

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getConfidenceReason() {
        return confidenceReason;
    }

    public void setConfidenceReason(String confidenceReason) {
        this.confidenceReason = confidenceReason;
    }

    public Boolean getLatest() {
        return latest;
    }

    public void setLatest(Boolean latest) {
        this.latest = latest;
    }

    public Boolean getSaved() {
        return saved;
    }

    public void setSaved(Boolean saved) {
        this.saved = saved;
    }

    public Instant getSupersededAt() {
        return supersededAt;
    }

    public void setSupersededAt(Instant supersededAt) {
        this.supersededAt = supersededAt;
    }

    public String getWarningsJson() {
        return warningsJson;
    }

    public String getExtraJson() {
        return extraJson;
    }

    public void setExtraJson(String extraJson) {
        this.extraJson = extraJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
