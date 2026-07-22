package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateLureRequest;
import com.lurepilot.backend.dto.LureResponse;
import com.lurepilot.backend.service.LureService;
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
@RequestMapping("/api/lures")
public class LureController {

    private final LureService lureService;

    public LureController(LureService lureService) {
        this.lureService = lureService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LureResponse createLure(@Valid @RequestBody CreateLureRequest request) {
        return lureService.createLure(request);
    }

    @GetMapping
    public List<LureResponse> getAllLures() {
        return lureService.getAllLures();
    }

    @GetMapping("/{id}")
    public LureResponse getLureById(@PathVariable Long id) {
        return lureService.getLureById(id);
    }

    @PutMapping("/{id}")
    public LureResponse updateLure(@PathVariable Long id, @Valid @RequestBody CreateLureRequest request) {
        return lureService.updateLure(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLure(@PathVariable Long id) {
        lureService.deleteLure(id);
    }
}
