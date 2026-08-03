package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateLureLibraryItemRequest;
import com.lurepilot.backend.dto.LureLibraryItemResponse;
import com.lurepilot.backend.dto.LureLibraryItemSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class LureLibraryItemService {

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "id", "id",
            "name", "name",
            "type", "type",
            "difficulty", "difficulty",
            "effectiveness", "effectiveness",
            "createdat", "createdAt"
    );

    private final LureLibraryItemRepository lureLibraryItemRepository;
    private final ListProjectionService listProjectionService;

    public LureLibraryItemService(LureLibraryItemRepository lureLibraryItemRepository, ListProjectionService listProjectionService) {
        this.lureLibraryItemRepository = lureLibraryItemRepository;
        this.listProjectionService = listProjectionService;
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
                request.idealConditions(),
                request.actionIconUrl(),
                request.actionImageUrl()
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
        Specification<LureLibraryItem> specification = Specification.allOf(
                SearchSpecifications.containsAny(
                        q,
                        "name",
                        "type",
                        "difficulty",
                        "effectiveness",
                        "description",
                        "usageNotes",
                        "actionType",
                        "idealConditions"
                ),
                SearchSpecifications.equalsIgnoreCase(type, "type"),
                SearchSpecifications.equalsIgnoreCase(difficulty, "difficulty"),
                SearchSpecifications.equalsIgnoreCase(effectiveness, "effectiveness")
        );
        Pageable pageable = ListQuerySupport.toPageable(page, size, sortBy, sortDirection, SORT_FIELDS);

        return listProjectionService.findLureLibraryItemSummaries(specification, pageable);
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
        item.setActionIconUrl(request.actionIconUrl());
        item.setActionImageUrl(request.actionImageUrl());

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
                item.getActionIconUrl(),
                item.getActionImageUrl(),
                item.getCreatedAt()
        );
    }

}
