package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishSpeciesRequest;
import com.lurepilot.backend.dto.FishSpeciesResponse;
import com.lurepilot.backend.dto.FishSpeciesSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.model.FishSpecies;
import com.lurepilot.backend.repository.FishSpeciesRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class FishSpeciesService {

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "id", "id",
            "name", "name",
            "waterenvironment", "waterEnvironment",
            "strikezone", "strikeZone",
            "createdat", "createdAt"
    );

    private final FishSpeciesRepository fishSpeciesRepository;
    private final ListProjectionService listProjectionService;

    public FishSpeciesService(FishSpeciesRepository fishSpeciesRepository, ListProjectionService listProjectionService) {
        this.fishSpeciesRepository = fishSpeciesRepository;
        this.listProjectionService = listProjectionService;
    }

    public FishSpeciesResponse createFishSpecies(CreateFishSpeciesRequest request) {
        FishSpecies fishSpecies = new FishSpecies(
                request.name(),
                request.waterEnvironment(),
                request.description(),
                request.imageUrl(),
                request.habitatNotes(),
                request.activeTimes(),
                request.strikeZone(),
                request.commonZones(),
                request.favoriteLures()
        );

        return toResponse(fishSpeciesRepository.save(fishSpecies));
    }

    public PagedResponse<FishSpeciesSummaryResponse> getAllFishSpecies() {
        return searchFishSpecies(null, null, null, 0, 20, "id", "asc");
    }

    public PagedResponse<FishSpeciesSummaryResponse> searchFishSpecies(
            String q,
            String strikeZone,
            String waterEnvironment,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Specification<FishSpecies> specification = Specification.allOf(
                SearchSpecifications.containsAny(
                        q,
                        "name",
                        "waterEnvironment",
                        "description",
                        "habitatNotes",
                        "activeTimes",
                        "strikeZone",
                        "commonZones",
                        "favoriteLures"
                ),
                SearchSpecifications.contains(strikeZone, "strikeZone"),
                SearchSpecifications.equalsIgnoreCase(waterEnvironment, "waterEnvironment")
        );
        Pageable pageable = ListQuerySupport.toPageable(page, size, sortBy, sortDirection, SORT_FIELDS);

        return listProjectionService.findFishSpeciesSummaries(specification, pageable);
    }

    public FishSpeciesResponse getFishSpeciesById(Long id) {
        return fishSpeciesRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fish species not found"));
    }

    public FishSpeciesResponse updateFishSpecies(Long id, CreateFishSpeciesRequest request) {
        FishSpecies fishSpecies = fishSpeciesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fish species not found"));

        fishSpecies.setName(request.name());
        fishSpecies.setWaterEnvironment(request.waterEnvironment());
        fishSpecies.setDescription(request.description());
        fishSpecies.setImageUrl(request.imageUrl());
        fishSpecies.setHabitatNotes(request.habitatNotes());
        fishSpecies.setActiveTimes(request.activeTimes());
        fishSpecies.setStrikeZone(request.strikeZone());
        fishSpecies.setCommonZones(request.commonZones());
        fishSpecies.setFavoriteLures(request.favoriteLures());

        return toResponse(fishSpeciesRepository.save(fishSpecies));
    }

    public void deleteFishSpecies(Long id) {
        if (!fishSpeciesRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fish species not found");
        }

        fishSpeciesRepository.deleteById(id);
    }

    private FishSpeciesResponse toResponse(FishSpecies fishSpecies) {
        return new FishSpeciesResponse(
                fishSpecies.getId(),
                fishSpecies.getName(),
                fishSpecies.getWaterEnvironment(),
                fishSpecies.getDescription(),
                fishSpecies.getImageUrl(),
                fishSpecies.getHabitatNotes(),
                fishSpecies.getActiveTimes(),
                fishSpecies.getStrikeZone(),
                fishSpecies.getCommonZones(),
                fishSpecies.getFavoriteLures(),
                fishSpecies.getCreatedAt()
        );
    }

}
