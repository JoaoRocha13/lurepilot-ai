package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateSessionEventRequest;
import com.lurepilot.backend.dto.SessionEventResponse;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.SessionEvent;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.SessionEventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SessionEventService {

    private final SessionEventRepository sessionEventRepository;
    private final FishingSessionRepository fishingSessionRepository;

    public SessionEventService(SessionEventRepository sessionEventRepository, FishingSessionRepository fishingSessionRepository) {
        this.sessionEventRepository = sessionEventRepository;
        this.fishingSessionRepository = fishingSessionRepository;
    }

    public SessionEventResponse createEvent(Long sessionId, CreateSessionEventRequest request) {
        FishingSession session = fishingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));

        SessionEvent sessionEvent = new SessionEvent(
                session,
                request.eventTime(),
                request.eventType(),
                request.description()
        );

        return toResponse(sessionEventRepository.save(sessionEvent));
    }

    public List<SessionEventResponse> getEventsBySession(Long sessionId) {
        if (!fishingSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        return sessionEventRepository.findBySessionIdOrderByEventTimeAscIdAsc(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private SessionEventResponse toResponse(SessionEvent sessionEvent) {
        return new SessionEventResponse(
                sessionEvent.getId(),
                sessionEvent.getSession().getId(),
                sessionEvent.getEventTime(),
                sessionEvent.getEventType(),
                sessionEvent.getDescription()
        );
    }
}
