package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishSpeciesRequest;
import com.lurepilot.backend.dto.FishSpeciesResponse;
import com.lurepilot.backend.dto.FishSpeciesSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.model.FishSpecies;
import com.lurepilot.backend.repository.FishSpeciesRepository;
import org.springframework.data.domain.Page;
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
            "strikezone", "strikeZone",
            "createdat", "createdAt"
    );

    private final FishSpeciesRepository fishSpeciesRepository;

    public FishSpeciesService(FishSpeciesRepository fishSpeciesRepository) {
        this.fishSpeciesRepository = fishSpeciesRepository;
    }

    public FishSpeciesResponse createFishSpecies(CreateFishSpeciesRequest request) {
        FishSpecies fishSpecies = new FishSpecies(
                request.name(),
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
        return searchFishSpecies(null, null, 0, 20, "id", "asc");
    }

    public PagedResponse<FishSpeciesSummaryResponse> searchFishSpecies(
            String q,
            String strikeZone,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Specification<FishSpecies> specification = Specification.allOf(
                SearchSpecifications.containsAny(
                        q,
                        "name",
                        "description",
                        "habitatNotes",
                        "activeTimes",
                        "strikeZone",
                        "commonZones",
                        "favoriteLures"
                ),
                SearchSpecifications.contains(strikeZone, "strikeZone")
        );
        Pageable pageable = ListQuerySupport.toPageable(page, size, sortBy, sortDirection, SORT_FIELDS);
        Page<FishSpecies> fishSpecies = fishSpeciesRepository.findAll(specification, pageable);

        return ListQuerySupport.toPagedResponse(fishSpecies, this::toSummaryResponse);
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

    private FishSpeciesSummaryResponse toSummaryResponse(FishSpecies fishSpecies) {
        return new FishSpeciesSummaryResponse(
                fishSpecies.getId(),
                fishSpecies.getName(),
                fishSpecies.getImageUrl(),
                fishSpecies.getStrikeZone(),
                fishSpecies.getFavoriteLures()
        );
    }

}
