package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateLureRequest;
import com.lurepilot.backend.dto.LureBoxItemSummaryResponse;
import com.lurepilot.backend.dto.LureResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import com.lurepilot.backend.repository.LureRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class LureService {

    private static final Map<String, String> SORT_FIELDS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("name", "name"),
            Map.entry("type", "type"),
            Map.entry("brand", "brand"),
            Map.entry("targetspecies", "targetSpecies"),
            Map.entry("watertype", "waterType"),
            Map.entry("active", "active"),
            Map.entry("quantity", "quantity"),
            Map.entry("condition", "condition"),
            Map.entry("libraryitemname", "libraryItem.name"),
            Map.entry("createdat", "createdAt")
    );

    private final LureRepository lureRepository;
    private final LureLibraryItemRepository lureLibraryItemRepository;
    private final ListProjectionService listProjectionService;

    public LureService(LureRepository lureRepository, LureLibraryItemRepository lureLibraryItemRepository, ListProjectionService listProjectionService) {
        this.lureRepository = lureRepository;
        this.lureLibraryItemRepository = lureLibraryItemRepository;
        this.listProjectionService = listProjectionService;
    }

    public LureResponse createLure(CreateLureRequest request) {
        Lure lure = new Lure(
                request.name(),
                request.type(),
                request.color(),
                request.size(),
                request.weight(),
                request.brand(),
                request.notes(),
                request.targetSpecies(),
                request.waterType()
        );
        lure.setLibraryItem(findLibraryItemOrNull(request.libraryItemId()));
        applyInventoryFields(lure, request);

        return toResponse(lureRepository.save(lure));
    }

    public PagedResponse<LureBoxItemSummaryResponse> getAllLures() {
        return searchLures(null, null, null, null, null, null, null, null, null, 0, 20, "id", "asc");
    }

    public PagedResponse<LureBoxItemSummaryResponse> searchLures(
            String q,
            String type,
            String waterType,
            String targetSpecies,
            String brand,
            Long libraryItemId,
            Boolean active,
            String condition,
            Integer minQuantity,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Specification<Lure> specification = Specification.allOf(
                SearchSpecifications.containsAny(
                        q,
                        "name",
                        "type",
                        "color",
                        "size",
                        "brand",
                        "notes",
                        "targetSpecies",
                        "waterType",
                        "libraryItem.name",
                        "condition",
                        "personalNotes",
                        "favoriteForSpecies",
                        "favoriteForSpot"
                ),
                SearchSpecifications.equalsIgnoreCase(type, "type"),
                SearchSpecifications.equalsIgnoreCase(waterType, "waterType"),
                SearchSpecifications.contains(targetSpecies, "targetSpecies"),
                SearchSpecifications.contains(brand, "brand"),
                SearchSpecifications.equalsValue(libraryItemId, "libraryItem.id"),
                SearchSpecifications.isActive(active),
                SearchSpecifications.equalsIgnoreCase(condition, "condition"),
                SearchSpecifications.quantityAtLeast(minQuantity)
        );
        Pageable pageable = ListQuerySupport.toPageable(page, size, sortBy, sortDirection, SORT_FIELDS);

        return listProjectionService.findLureBoxSummaries(specification, pageable);
    }

    public LureResponse getLureById(Long id) {
        return lureRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure not found"));
    }

    public LureResponse updateLure(Long id, CreateLureRequest request) {
        Lure lure = lureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure not found"));

        lure.setName(request.name());
        lure.setType(request.type());
        lure.setColor(request.color());
        lure.setSize(request.size());
        lure.setWeight(request.weight());
        lure.setBrand(request.brand());
        lure.setNotes(request.notes());
        lure.setLibraryItem(findLibraryItemOrNull(request.libraryItemId()));
        lure.setTargetSpecies(request.targetSpecies());
        lure.setWaterType(request.waterType());
        applyInventoryFields(lure, request);

        return toResponse(lureRepository.save(lure));
    }

    public void deleteLure(Long id) {
        if (!lureRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure not found");
        }

        lureRepository.deleteById(id);
    }

    private LureLibraryItem findLibraryItemOrNull(Long libraryItemId) {
        if (libraryItemId == null) {
            return null;
        }

        return lureLibraryItemRepository.findById(libraryItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure library item not found"));
    }

    private LureResponse toResponse(Lure lure) {
        LureLibraryItem libraryItem = lure.getLibraryItem();

        return new LureResponse(
                lure.getId(),
                lure.getName(),
                lure.getType(),
                lure.getColor(),
                lure.getSize(),
                lure.getWeight(),
                lure.getBrand(),
                lure.getNotes(),
                libraryItem == null ? null : libraryItem.getId(),
                libraryItem == null ? null : libraryItem.getName(),
                lure.getTargetSpecies(),
                lure.getWaterType(),
                activeOrDefault(lure),
                quantityOrDefault(lure),
                lure.getCondition(),
                lure.getPersonalNotes(),
                lure.getFavoriteForSpecies(),
                lure.getFavoriteForSpot(),
                lure.getCreatedAt()
        );
    }

    private void applyInventoryFields(Lure lure, CreateLureRequest request) {
        lure.setActive(request.active() == null ? true : request.active());
        lure.setQuantity(request.quantity() == null ? 1 : request.quantity());
        lure.setCondition(request.condition());
        lure.setPersonalNotes(request.personalNotes());
        lure.setFavoriteForSpecies(request.favoriteForSpecies());
        lure.setFavoriteForSpot(request.favoriteForSpot());
    }

    private Boolean activeOrDefault(Lure lure) {
        return lure.getActive() == null ? true : lure.getActive();
    }

    private Integer quantityOrDefault(Lure lure) {
        return lure.getQuantity() == null ? 1 : lure.getQuantity();
    }

}
