package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.IpmaLocationOptionResponse;
import com.lurepilot.backend.service.IpmaLocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather-locations/ipma")
public class IpmaLocationController {

    private final IpmaLocationService ipmaLocationService;

    public IpmaLocationController(IpmaLocationService ipmaLocationService) {
        this.ipmaLocationService = ipmaLocationService;
    }

    @GetMapping
    public List<IpmaLocationOptionResponse> getLocations() {
        return ipmaLocationService.getLocations();
    }

    @GetMapping("/search")
    public List<IpmaLocationOptionResponse> searchLocations(@RequestParam String query) {
        return ipmaLocationService.searchLocations(query);
    }
}
