package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateLureRequest;
import com.lurepilot.backend.dto.LureResponse;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.repository.LureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LureService {

    private final LureRepository lureRepository;

    public LureService(LureRepository lureRepository) {
        this.lureRepository = lureRepository;
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

        return toResponse(lureRepository.save(lure));
    }

    public List<LureResponse> getAllLures() {
        return lureRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
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
        lure.setTargetSpecies(request.targetSpecies());
        lure.setWaterType(request.waterType());

        return toResponse(lureRepository.save(lure));
    }

    public void deleteLure(Long id) {
        if (!lureRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lure not found");
        }

        lureRepository.deleteById(id);
    }

    private LureResponse toResponse(Lure lure) {
        return new LureResponse(
                lure.getId(),
                lure.getName(),
                lure.getType(),
                lure.getColor(),
                lure.getSize(),
                lure.getWeight(),
                lure.getBrand(),
                lure.getNotes(),
                lure.getTargetSpecies(),
                lure.getWaterType(),
                lure.getCreatedAt()
        );
    }
}
