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
