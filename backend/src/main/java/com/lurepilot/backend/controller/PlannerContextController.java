package com.lurepilot.backend.controller;

import com.lurepilot.backend.dto.PlannerContextResponse;
import com.lurepilot.backend.service.PlannerContextService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plans")
public class PlannerContextController {

    private final PlannerContextService plannerContextService;

    public PlannerContextController(PlannerContextService plannerContextService) {
        this.plannerContextService = plannerContextService;
    }

    @GetMapping("/{id}/context")
    public PlannerContextResponse getPlannerContext(@PathVariable Long id) {
        return plannerContextService.buildContext(id);
    }
}
