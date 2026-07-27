package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateFishingSessionRequest;
import com.lurepilot.backend.dto.FinishFishingSessionRequest;
import com.lurepilot.backend.dto.FishingSessionResponse;
import com.lurepilot.backend.dto.StartFishingSessionRequest;
import com.lurepilot.backend.service.FishingSessionService;
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
@RequestMapping("/api/sessions")
public class FishingSessionController {

    private final FishingSessionService fishingSessionService;

    public FishingSessionController(FishingSessionService fishingSessionService) {
        this.fishingSessionService = fishingSessionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FishingSessionResponse createSession(@Valid @RequestBody CreateFishingSessionRequest request) {
        return fishingSessionService.createSession(request);
    }

    @GetMapping
    public List<FishingSessionResponse> getAllSessions() {
        return fishingSessionService.getAllSessions();
    }

    @GetMapping("/{id}")
    public FishingSessionResponse getSessionById(@PathVariable Long id) {
        return fishingSessionService.getSessionById(id);
    }

    @PutMapping("/{id}")
    public FishingSessionResponse updateSession(@PathVariable Long id, @Valid @RequestBody CreateFishingSessionRequest request) {
        return fishingSessionService.updateSession(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@PathVariable Long id) {
        fishingSessionService.deleteSession(id);
    }

    @PostMapping("/{id}/start")
    public FishingSessionResponse startSession(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) StartFishingSessionRequest request
    ) {
        return fishingSessionService.startSession(id, request);
    }

    @PostMapping("/{id}/finish")
    public FishingSessionResponse finishSession(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) FinishFishingSessionRequest request
    ) {
        return fishingSessionService.finishSession(id, request);
    }
}
