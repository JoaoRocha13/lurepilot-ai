package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishingSpotRequest;
import com.lurepilot.backend.dto.FishingSpotResponse;
import com.lurepilot.backend.dto.FishingSpotSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.FishingSpotRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class FishingSpotService {

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "id", "id",
            "name", "name",
            "watertype", "waterType",
            "favoritespecies", "favoriteSpecies",
            "createdat", "createdAt"
    );

    private final FishingSpotRepository fishingSpotRepository;

    public FishingSpotService(FishingSpotRepository fishingSpotRepository) {
        this.fishingSpotRepository = fishingSpotRepository;
    }

    public FishingSpotResponse createSpot(CreateFishingSpotRequest request) {
        FishingSpot fishingSpot = new FishingSpot(
                request.name(),
                request.description(),
                request.latitude(),
                request.longitude(),
                request.waterType(),
                request.favoriteSpecies()
        );

        return toResponse(fishingSpotRepository.save(fishingSpot));
    }

    public PagedResponse<FishingSpotSummaryResponse> getAllSpots() {
        return searchSpots(null, null, null, 0, 20, "id", "asc");
    }

    public PagedResponse<FishingSpotSummaryResponse> searchSpots(
            String q,
            String waterType,
            String favoriteSpecies,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Specification<FishingSpot> specification = Specification.allOf(
                SearchSpecifications.containsAny(q, "name", "description", "waterType", "favoriteSpecies"),
                SearchSpecifications.equalsIgnoreCase(waterType, "waterType"),
                SearchSpecifications.contains(favoriteSpecies, "favoriteSpecies")
        );
        Pageable pageable = ListQuerySupport.toPageable(page, size, sortBy, sortDirection, SORT_FIELDS);
        Page<FishingSpot> spots = fishingSpotRepository.findAll(specification, pageable);

        return ListQuerySupport.toPagedResponse(spots, this::toSummaryResponse);
    }

    public FishingSpotResponse getSpotById(Long id) {
        return fishingSpotRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing spot not found"));
    }

    public FishingSpotResponse updateSpot(Long id, CreateFishingSpotRequest request) {
        FishingSpot fishingSpot = fishingSpotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing spot not found"));

        fishingSpot.setName(request.name());
        fishingSpot.setDescription(request.description());
        fishingSpot.setLatitude(request.latitude());
        fishingSpot.setLongitude(request.longitude());
        fishingSpot.setWaterType(request.waterType());
        fishingSpot.setFavoriteSpecies(request.favoriteSpecies());

        return toResponse(fishingSpotRepository.save(fishingSpot));
    }

    public void deleteSpot(Long id) {
        if (!fishingSpotRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fishing spot not found");
        }

        fishingSpotRepository.deleteById(id);
    }

    private FishingSpotResponse toResponse(FishingSpot fishingSpot) {
        return new FishingSpotResponse(
                fishingSpot.getId(),
                fishingSpot.getName(),
                fishingSpot.getDescription(),
                fishingSpot.getLatitude(),
                fishingSpot.getLongitude(),
                fishingSpot.getWaterType(),
                fishingSpot.getFavoriteSpecies(),
                fishingSpot.getCreatedAt()
        );
    }

    private FishingSpotSummaryResponse toSummaryResponse(FishingSpot fishingSpot) {
        return new FishingSpotSummaryResponse(
                fishingSpot.getId(),
                fishingSpot.getName(),
                fishingSpot.getLatitude(),
                fishingSpot.getLongitude(),
                fishingSpot.getWaterType(),
                fishingSpot.getFavoriteSpecies()
        );
    }

}
