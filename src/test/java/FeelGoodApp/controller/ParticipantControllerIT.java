package FeelGoodApp.controller;

import FeelGoodApp.dto.EventInfo;
import FeelGoodApp.dto.MusicVenueInfo;
import FeelGoodApp.dto.ParticipantInfo;
import FeelGoodApp.dto.PerformerInfo;
import FeelGoodApp.dto.command.*;
import FeelGoodApp.exceptionhandling.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static FeelGoodApp.domain.enums.GenreType.POST_ROCK;
import static FeelGoodApp.domain.enums.PartnerLevel.TIER_1;
import static FeelGoodApp.domain.enums.VenueType.BANDSTAND;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ParticipantControllerIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void testSaveParticipant_Success() {
        restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), EventInfo.class);

        ResponseEntity<ParticipantInfo> resultResponseEntity = restTemplate.postForEntity("/api/participants/1",
                new ParticipantCreateCommand("John Doe", "jd@gmail.com"), ParticipantInfo.class);

        ParticipantInfo participantInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        assertThat(participantInfo.getName()).isEqualTo("John Doe");
        assertThat(participantInfo.getEvent().getTicketCounter()).isEqualTo(201);
    }

    @Test
    void testSaveEvent_AllValidation() {
        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/participants/1",
                new ParticipantCreateCommand("", "jd_gmail.com"), ValidationError[].class);

        List<String> fields = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .map(ValidationError::getField)
                .collect(Collectors.toList());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(fields).contains("name", "email");
    }

    @Test
    void testSaveParticipant_EventIsNotAvailableForPublicException() {
        restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, false), EventInfo.class);

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/participants/1",
                new ParticipantCreateCommand("John Doe", "jd@gmail.com"), ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("eventId");
        assertThat(validationError.getErrorMessage()).contains("Event with id 1 is not available for public.");
    }

    @Test
    void testSaveParticipant_PastEventDateException() {
        restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), EventInfo.class);
        restTemplate.put("/api/events/1", new EventDateUpdateCommand(LocalDateTime.now().minusDays(1)), Void.class);


        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/participants/1",
                new ParticipantCreateCommand("John Doe", "jd@gmail.com"), ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("eventId");
        assertThat(validationError.getErrorMessage()).contains("Event with id 1 is a past event.");
    }

    @Test
    void testGetParticipantsByEvent_Empty() {
        restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), EventInfo.class);

        ResponseEntity<ParticipantInfo[]> resultResponseEntity = restTemplate.getForEntity("/api/participants/1", ParticipantInfo[].class);
        List<ParticipantInfo> participantInfos = List.of(Objects.requireNonNull(resultResponseEntity.getBody()));

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(participantInfos).isEmpty();
    }

    @Test
    void testGetParticipantsByEvent_TwoParticipants() {
        testSaveParticipant_Success();

        restTemplate.postForEntity("/api/participants/1",
                new ParticipantCreateCommand("Dohn Joe", "dj@gmail.com"), ParticipantInfo.class);

        ResponseEntity<ParticipantInfo[]> resultResponseEntity = restTemplate.getForEntity("/api/participants/1", ParticipantInfo[].class);
        List<ParticipantInfo> participantInfos = List.of(Objects.requireNonNull(resultResponseEntity.getBody()));

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(participantInfos.size()).isEqualTo(2);
    }

    @Test
    void testGetParticipantsByEvent_DeletedEvent() {
        testSaveParticipant_Success();
        restTemplate.delete("/api/events/1");

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.getForEntity("/api/participants/1", ValidationError[].class);
        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("eventId");
        assertThat(validationError.getErrorMessage()).contains("Event with id 1 is not found.");
    }

    @BeforeEach
    void init() {
        restTemplate.postForEntity("/api/musicVenues",
                new MusicVenueCreateCommand("Budapest Park", "06-30-211-3221",
                        "Budapest, Soroksári út 60, 1095", 4000, BANDSTAND), MusicVenueInfo.class);

        restTemplate.postForEntity("/api/performers",
                new PerformerCreateCommand("Képzelt Város", "kepzelt.varos@gmail.com",
                        "06-30-211-5555", POST_ROCK, TIER_1), PerformerInfo.class);


        restTemplate.postForEntity("/api/events",
                new EventCreateCommand(LocalDateTime.now().plusMonths(1), 200, 1), EventInfo.class);
    }
}
