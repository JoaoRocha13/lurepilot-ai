package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CreateLureLibraryItemRequest;
import com.lurepilot.backend.dto.LureLibraryItemResponse;
import com.lurepilot.backend.service.LureLibraryItemService;
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
@RequestMapping("/api/lure-library")
public class LureLibraryItemController {

    private final LureLibraryItemService lureLibraryItemService;

    public LureLibraryItemController(LureLibraryItemService lureLibraryItemService) {
        this.lureLibraryItemService = lureLibraryItemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LureLibraryItemResponse createLureLibraryItem(@Valid @RequestBody CreateLureLibraryItemRequest request) {
        return lureLibraryItemService.createLureLibraryItem(request);
    }

    @GetMapping
    public List<LureLibraryItemResponse> getAllLureLibraryItems() {
        return lureLibraryItemService.getAllLureLibraryItems();
    }

    @GetMapping("/{id}")
    public LureLibraryItemResponse getLureLibraryItemById(@PathVariable Long id) {
        return lureLibraryItemService.getLureLibraryItemById(id);
    }

    @PutMapping("/{id}")
    public LureLibraryItemResponse updateLureLibraryItem(@PathVariable Long id, @Valid @RequestBody CreateLureLibraryItemRequest request) {
        return lureLibraryItemService.updateLureLibraryItem(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLureLibraryItem(@PathVariable Long id) {
        lureLibraryItemService.deleteLureLibraryItem(id);
    }
}
