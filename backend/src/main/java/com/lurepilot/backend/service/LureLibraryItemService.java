package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateLureLibraryItemRequest;
import com.lurepilot.backend.dto.LureLibraryItemResponse;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LureLibraryItemService {

    private final LureLibraryItemRepository lureLibraryItemRepository;

    public LureLibraryItemService(LureLibraryItemRepository lureLibraryItemRepository) {
        this.lureLibraryItemRepository = lureLibraryItemRepository;
    }

    public LureLibraryItemResponse createLureLibraryItem(CreateLureLibraryItemRequest request) {
        LureLibraryItem item = new LureLibraryItem(
                request.name(),
                request.type(),
                request.imageUrl(),
                request.difficulty(),
                request.effectiveness(),
                request.description(),
                request.usageNotes(),
                request.actionType(),
                request.idealConditions()
        );

        return toResponse(lureLibraryItemRepository.save(item));
    }

    public List<LureLibraryItemResponse> getAllLureLibraryItems() {
        return searchLureLibraryItems(null, null, null, null);
    }

    public List<LureLibraryItemResponse> searchLureLibraryItems(String q, String type, String difficulty, String effectiveness) {
        return lureLibraryItemRepository.findAll()
                .stream()
                .filter(item -> matchesQuery(
                        q,
                        item.getName(),
                        item.getType(),
                        item.getDifficulty(),
                        item.getEffectiveness(),
                        item.getDescription(),
                        item.getUsageNotes(),
                        item.getActionType(),
                        item.getIdealConditions()
                ))
                .filter(item -> matchesExact(type, item.getType()))
                .filter(item -> matchesExact(difficulty, item.getDifficulty()))
                .filter(item -> matchesExact(effectiveness, item.getEffectiveness()))
                .map(this::toResponse)
                .toList();
    }

    public LureLibraryItemResponse getLureLibraryItemById(Long id) {
        return lureLibraryItemRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure library item not found"));
    }

    public LureLibraryItemResponse updateLureLibraryItem(Long id, CreateLureLibraryItemRequest request) {
        LureLibraryItem item = lureLibraryItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure library item not found"));

        item.setName(request.name());
        item.setType(request.type());
        item.setImageUrl(request.imageUrl());
        item.setDifficulty(request.difficulty());
        item.setEffectiveness(request.effectiveness());
        item.setDescription(request.description());
        item.setUsageNotes(request.usageNotes());
        item.setActionType(request.actionType());
        item.setIdealConditions(request.idealConditions());

        return toResponse(lureLibraryItemRepository.save(item));
    }

    public void deleteLureLibraryItem(Long id) {
        if (!lureLibraryItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure library item not found");
        }

        lureLibraryItemRepository.deleteById(id);
    }

    private LureLibraryItemResponse toResponse(LureLibraryItem item) {
        return new LureLibraryItemResponse(
                item.getId(),
                item.getName(),
                item.getType(),
                item.getImageUrl(),
                item.getDifficulty(),
                item.getEffectiveness(),
                item.getDescription(),
                item.getUsageNotes(),
                item.getActionType(),
                item.getIdealConditions(),
                item.getCreatedAt()
        );
    }

    private boolean matchesQuery(String query, String... values) {
        if (query == null || query.isBlank()) {
            return true;
        }

        String normalizedQuery = normalize(query);
        for (String value : values) {
            if (value != null && normalize(value).contains(normalizedQuery)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesExact(String expected, String actual) {
        return expected == null || expected.isBlank() || normalize(expected).equals(normalize(actual));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
