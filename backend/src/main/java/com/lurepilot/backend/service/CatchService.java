package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CatchResponse;
import com.lurepilot.backend.dto.CreateCatchRequest;
import com.lurepilot.backend.model.Catch;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CatchService {

    private final CatchRepository catchRepository;
    private final FishingSessionRepository fishingSessionRepository;

    public CatchService(CatchRepository catchRepository, FishingSessionRepository fishingSessionRepository) {
        this.catchRepository = catchRepository;
        this.fishingSessionRepository = fishingSessionRepository;
    }

    public CatchResponse createCatch(Long sessionId, CreateCatchRequest request) {
        FishingSession session = fishingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));

        Catch catchRecord = new Catch(
                session,
                request.species(),
                request.quantity(),
                request.sizeCm(),
                request.weightKg(),
                request.released(),
                request.notes()
        );

        return toResponse(catchRepository.save(catchRecord));
    }

    public List<CatchResponse> getCatchesBySession(Long sessionId) {
        if (!fishingSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        return catchRepository.findBySessionIdOrderByIdAsc(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CatchResponse updateCatch(Long sessionId, Long catchId, CreateCatchRequest request) {
        Catch catchRecord = findCatchForSession(sessionId, catchId);

        catchRecord.setSpecies(request.species());
        catchRecord.setQuantity(request.quantity());
        catchRecord.setSizeCm(request.sizeCm());
        catchRecord.setWeightKg(request.weightKg());
        catchRecord.setReleased(request.released());
        catchRecord.setNotes(request.notes());

        return toResponse(catchRepository.save(catchRecord));
    }

    public void deleteCatch(Long sessionId, Long catchId) {
        catchRepository.delete(findCatchForSession(sessionId, catchId));
    }

    private Catch findCatchForSession(Long sessionId, Long catchId) {
        Catch catchRecord = catchRepository.findById(catchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catch not found"));

        if (!sessionId.equals(catchRecord.getSession().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catch not found for session");
        }

        return catchRecord;
    }

    private CatchResponse toResponse(Catch catchRecord) {
        return new CatchResponse(
                catchRecord.getId(),
                catchRecord.getSession().getId(),
                catchRecord.getSpecies(),
                catchRecord.getQuantity(),
                catchRecord.getSizeCm(),
                catchRecord.getWeightKg(),
                catchRecord.getReleased(),
                catchRecord.getNotes()
        );
    }
}
