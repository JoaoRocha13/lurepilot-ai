package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.CatchGalleryItemResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.service.CatchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    private final CatchService catchService;

    public GalleryController(CatchService catchService) {
        this.catchService = catchService;
    }

    @GetMapping("/catches")
    public PagedResponse<CatchGalleryItemResponse> getCatchGallery(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Long spotId,
            @RequestParam(required = false) Boolean released,
            @RequestParam(required = false) Boolean sessionSuccess,
            @RequestParam(defaultValue = "true") Boolean withPhotoOnly,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return catchService.searchGalleryCatches(
                q,
                species,
                sessionId,
                spotId,
                released,
                sessionSuccess,
                withPhotoOnly,
                dateFrom,
                dateTo,
                page,
                size,
                sortBy,
                sortDirection
        );
    }
}
