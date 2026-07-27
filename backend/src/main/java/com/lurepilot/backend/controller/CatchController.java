package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CatchResponse;
import com.lurepilot.backend.dto.CreateCatchRequest;
import com.lurepilot.backend.service.CatchService;
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
@RequestMapping("/api/sessions/{sessionId}/catches")
public class CatchController {

    private final CatchService catchService;

    public CatchController(CatchService catchService) {
        this.catchService = catchService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CatchResponse createCatch(@PathVariable Long sessionId, @Valid @RequestBody CreateCatchRequest request) {
        return catchService.createCatch(sessionId, request);
    }

    @GetMapping
    public List<CatchResponse> getCatchesBySession(@PathVariable Long sessionId) {
        return catchService.getCatchesBySession(sessionId);
    }

    @PutMapping("/{catchId}")
    public CatchResponse updateCatch(
            @PathVariable Long sessionId,
            @PathVariable Long catchId,
            @Valid @RequestBody CreateCatchRequest request
    ) {
        return catchService.updateCatch(sessionId, catchId, request);
    }

    @DeleteMapping("/{catchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCatch(@PathVariable Long sessionId, @PathVariable Long catchId) {
        catchService.deleteCatch(sessionId, catchId);
    }
}
