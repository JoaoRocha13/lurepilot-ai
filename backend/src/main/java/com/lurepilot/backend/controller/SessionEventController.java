package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateSessionEventRequest;
import com.lurepilot.backend.dto.SessionEventResponse;
import com.lurepilot.backend.service.SessionEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sessions/{sessionId}/events")
public class SessionEventController {

    private final SessionEventService sessionEventService;

    public SessionEventController(SessionEventService sessionEventService) {
        this.sessionEventService = sessionEventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionEventResponse createEvent(@PathVariable Long sessionId, @Valid @RequestBody CreateSessionEventRequest request) {
        return sessionEventService.createEvent(sessionId, request);
    }

    @GetMapping
    public List<SessionEventResponse> getEventsBySession(@PathVariable Long sessionId) {
        return sessionEventService.getEventsBySession(sessionId);
    }

    @PutMapping("/{eventId}")
    public SessionEventResponse updateEvent(
            @PathVariable Long sessionId,
            @PathVariable Long eventId,
            @Valid @RequestBody CreateSessionEventRequest request
    ) {
        return sessionEventService.updateEvent(sessionId, eventId, request);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long sessionId, @PathVariable Long eventId) {
        sessionEventService.deleteEvent(sessionId, eventId);
    }
}
