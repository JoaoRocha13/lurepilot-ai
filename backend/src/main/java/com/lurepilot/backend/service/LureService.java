package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateLureRequest;
import com.lurepilot.backend.dto.LureBoxItemSummaryResponse;
import com.lurepilot.backend.dto.LureResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import com.lurepilot.backend.repository.LureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class LureService {

    private final LureRepository lureRepository;
    private final LureLibraryItemRepository lureLibraryItemRepository;

    public LureService(LureRepository lureRepository, LureLibraryItemRepository lureLibraryItemRepository) {
        this.lureRepository = lureRepository;
        this.lureLibraryItemRepository = lureLibraryItemRepository;
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
        List<Lure> filteredLures = lureRepository.findAll()
                .stream()
                .filter(lure -> matchesQuery(
                        q,
                        lure.getName(),
                        lure.getType(),
                        lure.getColor(),
                        lure.getSize(),
                        lure.getBrand(),
                        lure.getNotes(),
                        lure.getTargetSpecies(),
                        lure.getWaterType(),
                        lure.getLibraryItem() == null ? null : lure.getLibraryItem().getName(),
                        lure.getCondition(),
                        lure.getPersonalNotes(),
                        lure.getFavoriteForSpecies(),
                        lure.getFavoriteForSpot()
                ))
                .filter(lure -> matchesExact(type, lure.getType()))
                .filter(lure -> matchesExact(waterType, lure.getWaterType()))
                .filter(lure -> matchesContains(targetSpecies, lure.getTargetSpecies()))
                .filter(lure -> matchesContains(brand, lure.getBrand()))
                .filter(lure -> libraryItemId == null || lure.getLibraryItem() != null && libraryItemId.equals(lure.getLibraryItem().getId()))
                .toList();

        List<Lure> sortedLures = filteredLures.stream()
                .filter(lure -> active == null || active.equals(activeOrDefault(lure)))
                .filter(lure -> matchesExact(condition, lure.getCondition()))
                .filter(lure -> minQuantity == null || quantityOrDefault(lure) >= minQuantity)
                .sorted(ListQuerySupport.applyDirection(lureComparator(sortBy), sortDirection))
                .toList();

        return ListQuerySupport.toPage(sortedLures, page, size, this::toSummaryResponse);
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

    private LureBoxItemSummaryResponse toSummaryResponse(Lure lure) {
        LureLibraryItem libraryItem = lure.getLibraryItem();

        return new LureBoxItemSummaryResponse(
                lure.getId(),
                lure.getName(),
                lure.getType(),
                lure.getColor(),
                lure.getSize(),
                lure.getBrand(),
                libraryItem == null ? null : libraryItem.getId(),
                libraryItem == null ? null : libraryItem.getName(),
                lure.getTargetSpecies(),
                lure.getWaterType(),
                activeOrDefault(lure),
                quantityOrDefault(lure),
                lure.getCondition()
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

    private Comparator<Lure> lureComparator(String sortBy) {
        return switch (normalize(sortBy)) {
            case "name" -> ListQuerySupport.comparing(Lure::getName);
            case "type" -> ListQuerySupport.comparing(Lure::getType);
            case "brand" -> ListQuerySupport.comparing(Lure::getBrand);
            case "targetspecies" -> ListQuerySupport.comparing(Lure::getTargetSpecies);
            case "watertype" -> ListQuerySupport.comparing(Lure::getWaterType);
            case "active" -> ListQuerySupport.comparing(Lure::getActive);
            case "quantity" -> ListQuerySupport.comparing(Lure::getQuantity);
            case "condition" -> ListQuerySupport.comparing(Lure::getCondition);
            case "createdat" -> ListQuerySupport.comparing(Lure::getCreatedAt);
            default -> ListQuerySupport.comparing(Lure::getId);
        };
    }

    private Boolean activeOrDefault(Lure lure) {
        return lure.getActive() == null ? true : lure.getActive();
    }

    private Integer quantityOrDefault(Lure lure) {
        return lure.getQuantity() == null ? 1 : lure.getQuantity();
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

    private boolean matchesContains(String expected, String actual) {
        return expected == null || expected.isBlank() || normalize(actual).contains(normalize(expected));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
