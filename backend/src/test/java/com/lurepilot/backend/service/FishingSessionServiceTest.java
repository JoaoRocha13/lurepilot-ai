package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.CreateFishingSessionRequest;
import com.lurepilot.backend.dto.FinishFishingSessionRequest;
import com.lurepilot.backend.dto.FishingSessionResponse;
import com.lurepilot.backend.dto.StartFishingSessionRequest;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSessionStatus;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.FishingSpotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FishingSessionServiceTest {

    @Mock
    private FishingSessionRepository fishingSessionRepository;

    @Mock
    private FishingSpotRepository fishingSpotRepository;

    @Mock
    private FishingPlanRepository fishingPlanRepository;

    @InjectMocks
    private FishingSessionService fishingSessionService;

    @Test
    void createSessionCalculatesDurationAcrossMidnight() {
        FishingSpot spot = new FishingSpot("Barragem Norte", null, 38.7, -9.1, "DAM", "BASS");
        CreateFishingSessionRequest request = new CreateFishingSessionRequest(
                1L,
                null,
                LocalDate.of(2026, 7, 23),
                LocalTime.of(23, 30),
                LocalTime.of(0, 15),
                "BASS",
                "CLEAR",
                "LOW",
                "Night session",
                true
        );

        when(fishingSpotRepository.findById(1L)).thenReturn(Optional.of(spot));
        when(fishingSessionRepository.save(any(FishingSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FishingSessionResponse response = fishingSessionService.createSession(request);

        assertThat(response.durationMinutes()).isEqualTo(45);
        assertThat(response.status()).isEqualTo("finished");
        assertThat(response.success()).isTrue();
    }

    @Test
    void startSessionRejectsAlreadyFinishedSession() {
        FishingSpot spot = new FishingSpot("Barragem Norte", null, 38.7, -9.1, "DAM", "BASS");
        FishingSession session = new FishingSession(
                spot,
                null,
                LocalDate.of(2026, 7, 23),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0),
                "BASS",
                "CLEAR",
                "LOW",
                null,
                true
        );
        session.setStatus(FishingSessionStatus.FINISHED);

        when(fishingSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> fishingSessionService.startSession(1L, new StartFishingSessionRequest(LocalTime.of(19, 30), null)))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void startSessionKeepsTheStartTimeEnteredWhenCreatingTheSession() {
        FishingSpot spot = new FishingSpot("Barragem Norte", null, 38.7, -9.1, "DAM", "BASS");
        FishingSession session = new FishingSession(
                spot,
                null,
                LocalDate.of(2026, 7, 23),
                LocalTime.of(19, 0),
                null,
                "BASS",
                "CLEAR",
                "LOW",
                null,
                null
        );
        session.setStatus(FishingSessionStatus.ACTIVE);

        when(fishingSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(fishingSessionRepository.save(any(FishingSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FishingSessionResponse response = fishingSessionService.startSession(1L, new StartFishingSessionRequest(null, null));

        assertThat(response.startTime()).isEqualTo(LocalTime.of(19, 0));
    }

    @Test
    void finishSessionStoresResultAndCalculatesDuration() {
        FishingSpot spot = new FishingSpot("Barragem Norte", null, 38.7, -9.1, "DAM", "BASS");
        FishingSession session = new FishingSession(
                spot,
                null,
                LocalDate.of(2026, 7, 23),
                LocalTime.of(19, 0),
                null,
                "BASS",
                "CLEAR",
                "LOW",
                null,
                null
        );
        FinishFishingSessionRequest request = new FinishFishingSessionRequest(
                LocalTime.of(21, 15),
                true,
                "One catch",
                "Vinyl worked best",
                4
        );

        when(fishingSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(fishingSessionRepository.save(any(FishingSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FishingSessionResponse response = fishingSessionService.finishSession(1L, request);

        assertThat(response.status()).isEqualTo("finished");
        assertThat(response.durationMinutes()).isEqualTo(135);
        assertThat(response.resultSummary()).isEqualTo("One catch");
        assertThat(response.finalNotes()).isEqualTo("Vinyl worked best");
        assertThat(response.rating()).isEqualTo(4);
    }
}
