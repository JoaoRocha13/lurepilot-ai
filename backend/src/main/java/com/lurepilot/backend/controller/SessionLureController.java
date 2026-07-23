package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.AddSessionLureRequest;
import com.lurepilot.backend.dto.SessionLureResponse;
import com.lurepilot.backend.service.SessionLureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sessions/{sessionId}/lures")
public class SessionLureController {

    private final SessionLureService sessionLureService;

    public SessionLureController(SessionLureService sessionLureService) {
        this.sessionLureService = sessionLureService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionLureResponse addLureToSession(@PathVariable Long sessionId, @Valid @RequestBody AddSessionLureRequest request) {
        return sessionLureService.addLureToSession(sessionId, request);
    }

    @GetMapping
    public List<SessionLureResponse> getLuresBySession(@PathVariable Long sessionId) {
        return sessionLureService.getLuresBySession(sessionId);
    }
}
