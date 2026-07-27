package com.lurepilot.backend.controller;

import com.lurepilot.backend.config.GlobalExceptionHandler;
import com.lurepilot.backend.dto.FishSpeciesResponse;
import com.lurepilot.backend.dto.FishingPlanResponse;
import com.lurepilot.backend.dto.FishingSessionResponse;
import com.lurepilot.backend.dto.FishingSpotResponse;
import com.lurepilot.backend.dto.LureLibraryItemResponse;
import com.lurepilot.backend.dto.LureResponse;
import com.lurepilot.backend.service.AiRecommendationService;
import com.lurepilot.backend.service.FishSpeciesService;
import com.lurepilot.backend.service.FishingPlanService;
import com.lurepilot.backend.service.FishingSessionService;
import com.lurepilot.backend.service.FishingSpotService;
import com.lurepilot.backend.service.LureLibraryItemService;
import com.lurepilot.backend.service.LureService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MainControllerMockMvcTest {

    @Test
    void listSpotsAcceptsSearchFilters() throws Exception {
        FishingSpotService service = mock(FishingSpotService.class);
        when(service.searchSpots("tejo", "RIVER", "Barbo")).thenReturn(List.of(
                new FishingSpotResponse(1L, "Rio Tejo", null, 39.0, -8.0, "RIVER", "Barbo", null)
        ));

        mockMvc(new FishingSpotController(service))
                .perform(get("/api/spots")
                        .param("q", "tejo")
                        .param("waterType", "RIVER")
                        .param("favoriteSpecies", "Barbo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rio Tejo"));

        verify(service).searchSpots("tejo", "RIVER", "Barbo");
    }

    @Test
    void listFishAcceptsSearchFilters() throws Exception {
        FishSpeciesService service = mock(FishSpeciesService.class);
        when(service.searchFishSpecies("achiga", "surface")).thenReturn(List.of(
                new FishSpeciesResponse(1L, "Achiga", null, null, null, null, "surface", null, null, null)
        ));

        mockMvc(new FishSpeciesController(service))
                .perform(get("/api/fish")
                        .param("q", "achiga")
                        .param("strikeZone", "surface"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Achiga"));

        verify(service).searchFishSpecies("achiga", "surface");
    }

    @Test
    void listLureLibraryAcceptsSearchFilters() throws Exception {
        LureLibraryItemService service = mock(LureLibraryItemService.class);
        when(service.searchLureLibraryItems("jerk", "JERKBAIT", "MEDIUM", "HIGH")).thenReturn(List.of(
                new LureLibraryItemResponse(1L, "Jerkbait", "JERKBAIT", null, "MEDIUM", "HIGH", null, null, null, null, null)
        ));

        mockMvc(new LureLibraryItemController(service))
                .perform(get("/api/lure-library")
                        .param("q", "jerk")
                        .param("type", "JERKBAIT")
                        .param("difficulty", "MEDIUM")
                        .param("effectiveness", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Jerkbait"));

        verify(service).searchLureLibraryItems("jerk", "JERKBAIT", "MEDIUM", "HIGH");
    }

    @Test
    void listLuresAcceptsSearchFilters() throws Exception {
        LureService service = mock(LureService.class);
        when(service.searchLures("rapala", "JERKBAIT", "DAM", "BASS", "Rapala", 3L)).thenReturn(List.of(
                new LureResponse(1L, "Rapala Shadow Rap", "JERKBAIT", "SILVER", "11cm", 13.0, "Rapala", null, 3L, "Jerkbait", "BASS", "DAM", null)
        ));

        mockMvc(new LureController(service))
                .perform(get("/api/lures")
                        .param("q", "rapala")
                        .param("type", "JERKBAIT")
                        .param("waterType", "DAM")
                        .param("targetSpecies", "BASS")
                        .param("brand", "Rapala")
                        .param("libraryItemId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rapala Shadow Rap"));

        verify(service).searchLures("rapala", "JERKBAIT", "DAM", "BASS", "Rapala", 3L);
    }

    @Test
    void listPlansAcceptsSearchFilters() throws Exception {
        FishingPlanService service = mock(FishingPlanService.class);
        LocalDate dateFrom = LocalDate.of(2026, 7, 1);
        LocalDate dateTo = LocalDate.of(2026, 7, 31);
        when(service.searchPlans("fim tarde", 1L, "BASS", "CLEAR", "LOW", dateFrom, dateTo)).thenReturn(List.of(
                new FishingPlanResponse(1L, 1L, "Barragem Norte", LocalDate.of(2026, 7, 24), LocalTime.of(19, 0), "BASS", "CLEAR", "LOW", "fim tarde", null)
        ));

        mockMvc(new FishingPlanController(service))
                .perform(get("/api/plans")
                        .param("q", "fim tarde")
                        .param("spotId", "1")
                        .param("targetSpecies", "BASS")
                        .param("waterClarity", "CLEAR")
                        .param("waterLevel", "LOW")
                        .param("dateFrom", "2026-07-01")
                        .param("dateTo", "2026-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].spotName").value("Barragem Norte"));

        verify(service).searchPlans("fim tarde", 1L, "BASS", "CLEAR", "LOW", dateFrom, dateTo);
    }

    @Test
    void listSessionsAcceptsSearchFilters() throws Exception {
        FishingSessionService service = mock(FishingSessionService.class);
        LocalDate dateFrom = LocalDate.of(2026, 7, 1);
        LocalDate dateTo = LocalDate.of(2026, 7, 31);
        when(service.searchSessions("pedra", 1L, 2L, "BASS", "CLEAR", "LOW", "finished", true, dateFrom, dateTo)).thenReturn(List.of(
                new FishingSessionResponse(1L, 1L, "Barragem Norte", 2L, LocalDate.of(2026, 7, 24), LocalTime.of(19, 0), LocalTime.of(21, 0), "finished", "BASS", "CLEAR", "LOW", "pedra", true, 120L, "Boa sessao", null, 4, null)
        ));

        mockMvc(new FishingSessionController(service))
                .perform(get("/api/sessions")
                        .param("q", "pedra")
                        .param("spotId", "1")
                        .param("planId", "2")
                        .param("targetSpecies", "BASS")
                        .param("waterClarity", "CLEAR")
                        .param("waterLevel", "LOW")
                        .param("status", "finished")
                        .param("success", "true")
                        .param("dateFrom", "2026-07-01")
                        .param("dateTo", "2026-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("finished"));

        verify(service).searchSessions("pedra", 1L, 2L, "BASS", "CLEAR", "LOW", "finished", true, dateFrom, dateTo);
    }

    @Test
    void createSpotWithInvalidBodyReturnsValidationErrors() throws Exception {
        FishingSpotService service = mock(FishingSpotService.class);

        mockMvc(new FishingSpotController(service))
                .perform(post("/api/spots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("name")))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("latitude")))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("longitude")))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("waterType")));
    }

    @Test
    void createAiPlanRecommendationWithInvalidBodyReturnsValidationErrors() throws Exception {
        AiRecommendationService service = mock(AiRecommendationService.class);

        mockMvc(new AiRecommendationController(service))
                .perform(post("/api/recommendations/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("planId")));
    }

    private MockMvc mockMvc(Object controller) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }
}
