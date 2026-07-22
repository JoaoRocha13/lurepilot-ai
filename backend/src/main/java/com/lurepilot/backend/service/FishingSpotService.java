package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishingSpotRequest;
import com.lurepilot.backend.dto.FishingSpotResponse;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.FishingSpotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FishingSpotService {

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

    public List<FishingSpotResponse> getAllSpots() {
        return fishingSpotRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
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
}
