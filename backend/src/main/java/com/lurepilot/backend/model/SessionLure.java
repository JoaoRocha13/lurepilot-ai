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

import java.time.LocalTime;

@Entity
@Table(name = "session_lures")
public class SessionLure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private FishingSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lure_id", nullable = false)
    private Lure lure;

    private LocalTime usedFrom;

    private LocalTime usedTo;

    @Column(length = 1000)
    private String resultNotes;

    public SessionLure() {
    }

    public SessionLure(FishingSession session, Lure lure, LocalTime usedFrom, LocalTime usedTo, String resultNotes) {
        this.session = session;
        this.lure = lure;
        this.usedFrom = usedFrom;
        this.usedTo = usedTo;
        this.resultNotes = resultNotes;
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

    public Lure getLure() {
        return lure;
    }

    public void setLure(Lure lure) {
        this.lure = lure;
    }

    public LocalTime getUsedFrom() {
        return usedFrom;
    }

    public void setUsedFrom(LocalTime usedFrom) {
        this.usedFrom = usedFrom;
    }

    public LocalTime getUsedTo() {
        return usedTo;
    }

    public void setUsedTo(LocalTime usedTo) {
        this.usedTo = usedTo;
    }

    public String getResultNotes() {
        return resultNotes;
    }

    public void setResultNotes(String resultNotes) {
        this.resultNotes = resultNotes;
    }
}
