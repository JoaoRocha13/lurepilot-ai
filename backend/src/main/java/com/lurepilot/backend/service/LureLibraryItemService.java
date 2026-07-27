package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateLureLibraryItemRequest;
import com.lurepilot.backend.dto.LureLibraryItemResponse;
import com.lurepilot.backend.dto.LureLibraryItemSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
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

    public PagedResponse<LureLibraryItemSummaryResponse> getAllLureLibraryItems() {
        return searchLureLibraryItems(null, null, null, null, 0, 20, "id", "asc");
    }

    public PagedResponse<LureLibraryItemSummaryResponse> searchLureLibraryItems(
            String q,
            String type,
            String difficulty,
            String effectiveness,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        List<LureLibraryItem> filteredItems = lureLibraryItemRepository.findAll()
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
                .toList();

        List<LureLibraryItem> sortedItems = filteredItems.stream()
                .sorted(ListQuerySupport.applyDirection(lureLibraryItemComparator(sortBy), sortDirection))
                .toList();

        return ListQuerySupport.toPage(sortedItems, page, size, this::toSummaryResponse);
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

    private LureLibraryItemSummaryResponse toSummaryResponse(LureLibraryItem item) {
        return new LureLibraryItemSummaryResponse(
                item.getId(),
                item.getName(),
                item.getType(),
                item.getImageUrl(),
                item.getDifficulty(),
                item.getEffectiveness(),
                item.getActionType()
        );
    }

    private Comparator<LureLibraryItem> lureLibraryItemComparator(String sortBy) {
        return switch (normalize(sortBy)) {
            case "name" -> ListQuerySupport.comparing(LureLibraryItem::getName);
            case "type" -> ListQuerySupport.comparing(LureLibraryItem::getType);
            case "difficulty" -> ListQuerySupport.comparing(LureLibraryItem::getDifficulty);
            case "effectiveness" -> ListQuerySupport.comparing(LureLibraryItem::getEffectiveness);
            case "createdat" -> ListQuerySupport.comparing(LureLibraryItem::getCreatedAt);
            default -> ListQuerySupport.comparing(LureLibraryItem::getId);
        };
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
