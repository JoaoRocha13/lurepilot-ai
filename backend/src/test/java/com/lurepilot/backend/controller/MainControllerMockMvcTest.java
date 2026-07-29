package com.lurepilot.backend.controller;

import com.lurepilot.backend.config.GlobalExceptionHandler;
import com.lurepilot.backend.dto.AnalyticsSummaryResponse;
import com.lurepilot.backend.dto.FishSpeciesResponse;
import com.lurepilot.backend.dto.FishSpeciesSummaryResponse;
import com.lurepilot.backend.dto.FishingPlanResponse;
import com.lurepilot.backend.dto.FishingPlanSummaryResponse;
import com.lurepilot.backend.dto.FishingSessionResponse;
import com.lurepilot.backend.dto.FishingSessionSummaryResponse;
import com.lurepilot.backend.dto.FishingSpotResponse;
import com.lurepilot.backend.dto.FishingSpotSummaryResponse;
import com.lurepilot.backend.dto.LureBoxItemSummaryResponse;
import com.lurepilot.backend.dto.LureLibraryItemResponse;
import com.lurepilot.backend.dto.LureLibraryItemSummaryResponse;
import com.lurepilot.backend.dto.PagedResponse;
import com.lurepilot.backend.dto.RecommendationExecutionResponse;
import com.lurepilot.backend.service.AiRecommendationService;
import com.lurepilot.backend.service.AnalyticsService;
import com.lurepilot.backend.service.FishSpeciesService;
import com.lurepilot.backend.service.FishingPlanService;
import com.lurepilot.backend.service.FishingSessionService;
import com.lurepilot.backend.service.FishingSpotService;
import com.lurepilot.backend.service.LureLibraryItemService;
import com.lurepilot.backend.service.LureService;
import com.lurepilot.backend.service.RecommendationExecutionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
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
        when(service.searchSpots("tejo", "RIVER", "Barbo", 1, 10, "name", "desc")).thenReturn(new PagedResponse<>(
                List.of(new FishingSpotSummaryResponse(1L, "Rio Tejo", 39.0, -8.0, "RIVER", "Barbo")),
                1,
                1,
                10,
                1,
                false,
                true
        ));

        mockMvc(new FishingSpotController(service))
                .perform(get("/api/spots")
                        .param("q", "tejo")
                        .param("waterType", "RIVER")
                        .param("favoriteSpecies", "Barbo")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Rio Tejo"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(service).searchSpots("tejo", "RIVER", "Barbo", 1, 10, "name", "desc");
    }

    @Test
    void listFishAcceptsSearchFilters() throws Exception {
        FishSpeciesService service = mock(FishSpeciesService.class);
        when(service.searchFishSpecies("achiga", "surface", 0, 20, "id", "asc")).thenReturn(new PagedResponse<>(
                List.of(new FishSpeciesSummaryResponse(1L, "Achiga", null, "surface", null)),
                1,
                0,
                20,
                1,
                false,
                false
        ));

        mockMvc(new FishSpeciesController(service))
                .perform(get("/api/fish")
                        .param("q", "achiga")
                        .param("strikeZone", "surface"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Achiga"));

        verify(service).searchFishSpecies("achiga", "surface", 0, 20, "id", "asc");
    }

    @Test
    void listLureLibraryAcceptsSearchFilters() throws Exception {
        LureLibraryItemService service = mock(LureLibraryItemService.class);
        when(service.searchLureLibraryItems("jerk", "JERKBAIT", "MEDIUM", "HIGH", 0, 20, "id", "asc")).thenReturn(new PagedResponse<>(
                List.of(new LureLibraryItemSummaryResponse(1L, "Jerkbait", "JERKBAIT", null, "MEDIUM", "HIGH", null)),
                1,
                0,
                20,
                1,
                false,
                false
        ));

        mockMvc(new LureLibraryItemController(service))
                .perform(get("/api/lure-library")
                        .param("q", "jerk")
                        .param("type", "JERKBAIT")
                        .param("difficulty", "MEDIUM")
                        .param("effectiveness", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Jerkbait"));

        verify(service).searchLureLibraryItems("jerk", "JERKBAIT", "MEDIUM", "HIGH", 0, 20, "id", "asc");
    }

    @Test
    void listLuresAcceptsSearchFilters() throws Exception {
        LureService service = mock(LureService.class);
        when(service.searchLures("rapala", "JERKBAIT", "DAM", "BASS", "Rapala", 3L, true, "GOOD", 1, 0, 20, "id", "asc")).thenReturn(new PagedResponse<>(
                List.of(new LureBoxItemSummaryResponse(1L, "Rapala Shadow Rap", "JERKBAIT", "SILVER", "11cm", "Rapala", 3L, "Jerkbait", "BASS", "DAM", true, 1, "GOOD")),
                1,
                0,
                20,
                1,
                false,
                false
        ));

        mockMvc(new LureController(service))
                .perform(get("/api/lures")
                        .param("q", "rapala")
                        .param("type", "JERKBAIT")
                        .param("waterType", "DAM")
                        .param("targetSpecies", "BASS")
                        .param("brand", "Rapala")
                        .param("libraryItemId", "3")
                        .param("active", "true")
                        .param("condition", "GOOD")
                        .param("minQuantity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Rapala Shadow Rap"))
                .andExpect(jsonPath("$.items[0].active").value(true))
                .andExpect(jsonPath("$.items[0].quantity").value(1));

        verify(service).searchLures("rapala", "JERKBAIT", "DAM", "BASS", "Rapala", 3L, true, "GOOD", 1, 0, 20, "id", "asc");
    }

    @Test
    void listPlansAcceptsSearchFilters() throws Exception {
        FishingPlanService service = mock(FishingPlanService.class);
        LocalDate dateFrom = LocalDate.of(2026, 7, 1);
        LocalDate dateTo = LocalDate.of(2026, 7, 31);
        when(service.searchPlans("fim tarde", 1L, "BASS", "CLEAR", "LOW", dateFrom, dateTo, 0, 20, "id", "asc")).thenReturn(new PagedResponse<>(
                List.of(new FishingPlanSummaryResponse(1L, 1L, "Barragem Norte", LocalDate.of(2026, 7, 24), LocalTime.of(19, 0), "BASS", "CLEAR", "LOW")),
                1,
                0,
                20,
                1,
                false,
                false
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
                .andExpect(jsonPath("$.items[0].spotName").value("Barragem Norte"));

        verify(service).searchPlans("fim tarde", 1L, "BASS", "CLEAR", "LOW", dateFrom, dateTo, 0, 20, "id", "asc");
    }

    @Test
    void listSessionsAcceptsSearchFilters() throws Exception {
        FishingSessionService service = mock(FishingSessionService.class);
        LocalDate dateFrom = LocalDate.of(2026, 7, 1);
        LocalDate dateTo = LocalDate.of(2026, 7, 31);
        when(service.searchSessions("pedra", 1L, 2L, "BASS", "CLEAR", "LOW", "finished", true, dateFrom, dateTo, 0, 20, "id", "asc")).thenReturn(new PagedResponse<>(
                List.of(new FishingSessionSummaryResponse(1L, 1L, "Barragem Norte", 2L, LocalDate.of(2026, 7, 24), LocalTime.of(19, 0), LocalTime.of(21, 0), "finished", "BASS", true, 4)),
                1,
                0,
                20,
                1,
                false,
                false
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
                .andExpect(jsonPath("$.items[0].status").value("finished"));

        verify(service).searchSessions("pedra", 1L, 2L, "BASS", "CLEAR", "LOW", "finished", true, dateFrom, dateTo, 0, 20, "id", "asc");
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

    @Test
    void createRecommendationExecutionAcceptsTrackingPayload() throws Exception {
        RecommendationExecutionService service = mock(RecommendationExecutionService.class);
        when(service.createExecution(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any())).thenReturn(new RecommendationExecutionResponse(
                5L,
                1L,
                2L,
                3L,
                "PLAN",
                1,
                "PLAN_A",
                true,
                "CATCH",
                true,
                4,
                LocalTime.of(19, 0),
                LocalTime.of(19, 20),
                "Resultou junto a estrutura.",
                Instant.parse("2026-07-29T14:00:00Z")
        ));

        mockMvc(new RecommendationExecutionController(service))
                .perform(post("/api/recommendations/1/executions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sessionId": 3,
                                  "recommendationStep": "planA",
                                  "followed": true,
                                  "result": "CATCH",
                                  "success": true,
                                  "rating": 4,
                                  "startedAt": "19:00",
                                  "endedAt": "19:20",
                                  "notes": "Resultou junto a estrutura."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recommendationStep").value("PLAN_A"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getAnalyticsSummaryReturnsBasicCounters() throws Exception {
        AnalyticsService service = mock(AnalyticsService.class);
        when(service.getSummary()).thenReturn(new AnalyticsSummaryResponse(
                10,
                8,
                4,
                40.0,
                3,
                5,
                3.5,
                2,
                2,
                1,
                100.0,
                50.0,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        ));

        mockMvc(new AnalyticsController(service))
                .perform(get("/api/analytics/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSessions").value(10))
                .andExpect(jsonPath("$.sessionSuccessRate").value(40.0))
                .andExpect(jsonPath("$.recommendationFollowRate").value(100.0));
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
