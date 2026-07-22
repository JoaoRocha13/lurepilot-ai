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
@Table(name = "session_events")
public class SessionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private FishingSession session;

    private LocalTime eventTime;

    @Column(nullable = false)
    private String eventType;

    @Column(length = 1000)
    private String description;

    public SessionEvent() {
    }

    public SessionEvent(FishingSession session, LocalTime eventTime, String eventType, String description) {
        this.session = session;
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.description = description;
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

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
