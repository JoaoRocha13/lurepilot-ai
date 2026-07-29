package com.lurepilot.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fishing_plan_lures", indexes = {
        @Index(name = "idx_plan_lures_plan", columnList = "plan_id"),
        @Index(name = "idx_plan_lures_lure", columnList = "lure_id")
})
public class FishingPlanLure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private FishingPlan plan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lure_id", nullable = false)
    private Lure lure;

    public FishingPlanLure() {
    }

    public FishingPlanLure(FishingPlan plan, Lure lure) {
        this.plan = plan;
        this.lure = lure;
    }

    public Long getId() {
        return id;
    }

    public FishingPlan getPlan() {
        return plan;
    }

    public void setPlan(FishingPlan plan) {
        this.plan = plan;
    }

    public Lure getLure() {
        return lure;
    }

    public void setLure(Lure lure) {
        this.lure = lure;
    }
}
