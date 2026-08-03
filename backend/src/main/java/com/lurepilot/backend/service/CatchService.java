package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CatchGalleryItemResponse;
import com.lurepilot.backend.dto.CatchResponse;
import com.lurepilot.backend.dto.CreateCatchRequest;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.model.Catch;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class CatchService {

    private static final Map<String, String> GALLERY_SORT_FIELDS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("catchid", "id"),
            Map.entry("sessiondate", "session.date"),
            Map.entry("sessionstarttime", "session.startTime"),
            Map.entry("spotname", "session.spot.name"),
            Map.entry("species", "species"),
            Map.entry("quantity", "quantity"),
            Map.entry("sizecm", "sizeCm"),
            Map.entry("weightkg", "weightKg"),
            Map.entry("released", "released")
    );

    private final CatchRepository catchRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final LureLibraryItemRepository lureLibraryItemRepository;
    private final ListProjectionService listProjectionService;

    public CatchService(
            CatchRepository catchRepository,
            FishingSessionRepository fishingSessionRepository,
            LureLibraryItemRepository lureLibraryItemRepository,
            ListProjectionService listProjectionService
    ) {
        this.catchRepository = catchRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.lureLibraryItemRepository = lureLibraryItemRepository;
        this.listProjectionService = listProjectionService;
    }

    public CatchResponse createCatch(Long sessionId, CreateCatchRequest request) {
        FishingSession session = fishingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found"));

        Catch catchRecord = new Catch(
                session,
                request.species(),
                request.quantity(),
                request.sizeCm(),
                request.weightKg(),
                request.released(),
                request.notes(),
                request.photoUrl(),
                request.photoThumbnailUrl(),
                request.photoCaption()
        );
        catchRecord.setLureLibraryItem(findLureLibraryItemOrNull(request.lureLibraryItemId()));

        return toResponse(catchRepository.save(catchRecord));
    }

    public List<CatchResponse> getCatchesBySession(Long sessionId) {
        if (!fishingSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing session not found");
        }

        return catchRepository.findBySessionIdOrderByIdAsc(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CatchResponse updateCatch(Long sessionId, Long catchId, CreateCatchRequest request) {
        Catch catchRecord = findCatchForSession(sessionId, catchId);

        catchRecord.setSpecies(request.species());
        catchRecord.setQuantity(request.quantity());
        catchRecord.setSizeCm(request.sizeCm());
        catchRecord.setWeightKg(request.weightKg());
        catchRecord.setReleased(request.released());
        catchRecord.setNotes(request.notes());
        catchRecord.setPhotoUrl(request.photoUrl());
        catchRecord.setPhotoThumbnailUrl(request.photoThumbnailUrl());
        catchRecord.setPhotoCaption(request.photoCaption());
        catchRecord.setLureLibraryItem(findLureLibraryItemOrNull(request.lureLibraryItemId()));

        return toResponse(catchRepository.save(catchRecord));
    }

    public PagedResponse<CatchGalleryItemResponse> searchGalleryCatches(
            String q,
            String species,
            Long sessionId,
            Long spotId,
            Boolean released,
            Boolean sessionSuccess,
            Boolean withPhotoOnly,
            LocalDate dateFrom,
            LocalDate dateTo,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Specification<Catch> specification = Specification.allOf(
                SearchSpecifications.containsAny(
                        q,
                        "species",
                        "notes",
                        "photoCaption",
                        "session.spot.name",
                        "session.targetSpecies",
                        "session.resultSummary"
                ),
                SearchSpecifications.contains(species, "species"),
                SearchSpecifications.equalsValue(sessionId, "session.id"),
                SearchSpecifications.equalsValue(spotId, "session.spot.id"),
                SearchSpecifications.equalsValue(released, "released"),
                SearchSpecifications.equalsValue(sessionSuccess, "session.success"),
                SearchSpecifications.dateFrom(dateFrom, "session.date"),
                SearchSpecifications.dateTo(dateTo, "session.date"),
                hasPhoto(withPhotoOnly)
        );
        Pageable pageable = ListQuerySupport.toPageable(page, size, sortBy, sortDirection, GALLERY_SORT_FIELDS);

        return listProjectionService.findCatchGalleryItems(specification, pageable);
    }

    public void deleteCatch(Long sessionId, Long catchId) {
        catchRepository.delete(findCatchForSession(sessionId, catchId));
    }

    private Catch findCatchForSession(Long sessionId, Long catchId) {
        Catch catchRecord = catchRepository.findById(catchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catch not found"));

        if (!sessionId.equals(catchRecord.getSession().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catch not found for session");
        }

        return catchRecord;
    }

    private CatchResponse toResponse(Catch catchRecord) {
        return new CatchResponse(
                catchRecord.getId(),
                catchRecord.getSession().getId(),
                catchRecord.getSpecies(),
                catchRecord.getQuantity(),
                catchRecord.getSizeCm(),
                catchRecord.getWeightKg(),
                catchRecord.getReleased(),
                catchRecord.getNotes(),
                catchRecord.getPhotoUrl(),
                catchRecord.getPhotoThumbnailUrl(),
                catchRecord.getPhotoCaption(),
                catchRecord.getLureLibraryItem() == null ? null : catchRecord.getLureLibraryItem().getId(),
                catchRecord.getLureLibraryItem() == null ? null : catchRecord.getLureLibraryItem().getName(),
                catchRecord.getLureLibraryItem() == null ? null : catchRecord.getLureLibraryItem().getImageUrl()
        );
    }

    private LureLibraryItem findLureLibraryItemOrNull(Long lureLibraryItemId) {
        if (lureLibraryItemId == null) {
            return null;
        }

        return lureLibraryItemRepository.findById(lureLibraryItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure library item not found"));
    }

    private Specification<Catch> hasPhoto(Boolean withPhotoOnly) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (!Boolean.TRUE.equals(withPhotoOnly)) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(
                    root.get("photoUrl").isNotNull(),
                    criteriaBuilder.notEqual(criteriaBuilder.trim(root.get("photoUrl")), "")
            );
        };
    }
}
