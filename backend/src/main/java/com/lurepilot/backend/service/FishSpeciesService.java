package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishSpeciesRequest;
import com.lurepilot.backend.dto.FishSpeciesResponse;
import com.lurepilot.backend.model.FishSpecies;
import com.lurepilot.backend.repository.FishSpeciesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FishSpeciesService {

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

    public List<FishSpeciesResponse> getAllFishSpecies() {
        return fishSpeciesRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FishSpeciesResponse getFishSpeciesById(Long id) {
        return fishSpeciesRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fish species not found"));
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
}
