package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.AddSessionLureRequest;
import com.lurepilot.backend.dto.SessionLureResponse;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.SessionLure;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.LureRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SessionLureService {

    private final SessionLureRepository sessionLureRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final LureRepository lureRepository;

    public SessionLureService(SessionLureRepository sessionLureRepository, FishingSessionRepository fishingSessionRepository, LureRepository lureRepository) {
        this.sessionLureRepository = sessionLureRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.lureRepository = lureRepository;
    }

    public SessionLureResponse addLureToSession(Long sessionId, AddSessionLureRequest request) {
        FishingSession session = fishingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));
        Lure lure = lureRepository.findById(request.lureId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure not found"));

        SessionLure sessionLure = new SessionLure(
                session,
                lure,
                request.usedFrom(),
                request.usedTo(),
                request.resultNotes()
        );

        return toResponse(sessionLureRepository.save(sessionLure));
    }

    public List<SessionLureResponse> getLuresBySession(Long sessionId) {
        if (!fishingSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        return sessionLureRepository.findBySessionIdOrderByUsedFromAscIdAsc(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SessionLureResponse updateSessionLure(Long sessionId, Long sessionLureId, AddSessionLureRequest request) {
        SessionLure sessionLure = findSessionLureForSession(sessionId, sessionLureId);
        Lure lure = lureRepository.findById(request.lureId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure not found"));

        sessionLure.setLure(lure);
        sessionLure.setUsedFrom(request.usedFrom());
        sessionLure.setUsedTo(request.usedTo());
        sessionLure.setResultNotes(request.resultNotes());

        return toResponse(sessionLureRepository.save(sessionLure));
    }

    public void deleteSessionLure(Long sessionId, Long sessionLureId) {
        sessionLureRepository.delete(findSessionLureForSession(sessionId, sessionLureId));
    }

    private SessionLure findSessionLureForSession(Long sessionId, Long sessionLureId) {
        SessionLure sessionLure = sessionLureRepository.findById(sessionLureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session lure not found"));

        if (!sessionId.equals(sessionLure.getSession().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session lure not found for session");
        }

        return sessionLure;
    }

    private SessionLureResponse toResponse(SessionLure sessionLure) {
        Lure lure = sessionLure.getLure();

        return new SessionLureResponse(
                sessionLure.getId(),
                sessionLure.getSession().getId(),
                lure.getId(),
                lure.getName(),
                lure.getType(),
                sessionLure.getUsedFrom(),
                sessionLure.getUsedTo(),
                sessionLure.getResultNotes()
        );
    }
}
