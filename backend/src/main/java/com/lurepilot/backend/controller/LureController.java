package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateLureRequest;
import com.lurepilot.backend.dto.LureBoxItemSummaryResponse;
import com.lurepilot.backend.dto.LureResponse;
import com.lurepilot.backend.dto.PagedResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/lures", "/api/lure-box"})
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
    public PagedResponse<LureBoxItemSummaryResponse> getAllLures(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String waterType,
            @RequestParam(required = false) String targetSpecies,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Long libraryItemId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return lureService.searchLures(q, type, waterType, targetSpecies, brand, libraryItemId, active, condition, minQuantity, page, size, sortBy, sortDirection);
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
