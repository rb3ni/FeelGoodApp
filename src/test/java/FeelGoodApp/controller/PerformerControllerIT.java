package FeelGoodApp.controller;

import FeelGoodApp.dto.EventInfo;
import FeelGoodApp.dto.MusicVenueInfo;
import FeelGoodApp.dto.PerformerInfo;
import FeelGoodApp.dto.command.*;
import FeelGoodApp.exceptionhandling.ValidationError;
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
import static FeelGoodApp.domain.enums.GenreType.ROCK;
import static FeelGoodApp.domain.enums.PartnerLevel.TIER_1;
import static FeelGoodApp.domain.enums.PartnerLevel.TIER_5;
import static FeelGoodApp.domain.enums.VenueType.BANDSTAND;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PerformerControllerIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void testSavePerformer_Success() {
        ResponseEntity<PerformerInfo> resultResponseEntity = restTemplate.postForEntity("/api/performers",
                new PerformerCreateCommand("Képzelt Város", "kepzelt.varos@gmail.com",
                        "06-30-211-5555", POST_ROCK, TIER_1), PerformerInfo.class);

        PerformerInfo performerInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        assertThat(performerInfo.getName()).isEqualTo("Képzelt Város");
        assertThat(performerInfo.getEmail()).isEqualTo("kepzelt.varos@gmail.com");
        assertThat(performerInfo.getGenre()).isEqualTo(POST_ROCK);
    }

    @Test
    void testSavePerformer_AllValidation() {
        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/performers",
                new PerformerCreateCommand("", "kepzelt.varos_gmail.com",
                        "06-30-211-5555-515415155154", null, null), ValidationError[].class);

        List<String> fields = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .map(ValidationError::getField)
                .collect(Collectors.toList());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(fields).contains("name", "email", "contactPhoneNumber", "genre", "partnerLevel");
    }

    @Test
    void testSavePerformer_PerformerNameNotUniqueException() {
        testSavePerformer_Success();

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/performers",
                new PerformerCreateCommand("Képzelt Város", "kepzelt.varos@gmail.com",
                        "06-30-211-5555", POST_ROCK, TIER_1), ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("name");
        assertThat(validationError.getErrorMessage()).contains("Performer name already exist");
    }

    @Test
    void testGetPerformers_Empty() {
        ResponseEntity<PerformerInfo[]> resultResponseEntity = restTemplate.getForEntity("/api/performers", PerformerInfo[].class);
        List<PerformerInfo> performerInfos = List.of(Objects.requireNonNull(resultResponseEntity.getBody()));

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(performerInfos).isEmpty();
    }

    @Test
    void testGetPerformers_TwoPerformers() {
        testSavePerformer_Success();
        restTemplate.postForEntity("/api/performers",
                new PerformerCreateCommand("Yara", "yara@gmail.com",
                        "06-30-211-5555", ROCK, TIER_5), PerformerInfo.class);

        ResponseEntity<PerformerInfo[]> resultResponseEntity = restTemplate.getForEntity("/api/performers", PerformerInfo[].class);
        List<PerformerInfo> performerInfos = List.of(Objects.requireNonNull(resultResponseEntity.getBody()));

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(performerInfos.size()).isEqualTo(2);
    }

    @Test
    void testGetPerformerById_Success() {
        testSavePerformer_Success();

        ResponseEntity<PerformerInfo> resultResponseEntity = restTemplate.getForEntity("/api/performers/1", PerformerInfo.class);
        PerformerInfo performerInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(performerInfo.getName()).isEqualTo("Képzelt Város");
    }

    @Test
    void testGetPerformerById_EventNotFoundException() {
        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.getForEntity("/api/performers/1",
                ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("performerId");
        assertThat(validationError.getErrorMessage()).contains("Performer with id 1 is not found.");
    }

    @Test
    void testModifyPerformerTier_Success() {
        testSavePerformer_Success();

        restTemplate.put("/api/performers/1", new PerformerPartnerLevelUpdateCommand(TIER_5), Void.class);

        ResponseEntity<PerformerInfo> resultResponseEntity = restTemplate.getForEntity("/api/performers/1", PerformerInfo.class);
        PerformerInfo performerInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(performerInfo.getPartnerLevel()).isEqualTo(TIER_5);
    }

    @Test
    void testModifyPerformerTier_NullTierValidation() {
        testSavePerformer_Success();

        restTemplate.put("/api/performers/1", new PerformerPartnerLevelUpdateCommand(null), Void.class);

        ResponseEntity<PerformerInfo> resultResponseEntity = restTemplate.getForEntity("/api/performers/1", PerformerInfo.class);
        PerformerInfo performerInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(performerInfo.getPartnerLevel()).isNotEqualTo(null);
        assertThat(performerInfo.getPartnerLevel()).isEqualTo(TIER_1);
    }

    @Test
    void testDeletePerformer_HeadlinerOnEvent() {
        testSavePerformer_Success();
        createEventForDeleteTests();

        restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), EventInfo.class);

        restTemplate.delete("/api/performers/1");

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.getForEntity("/api/events/1",
                ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("eventId");
        assertThat(validationError.getErrorMessage()).contains("Event with id 1 is not found.");
    }

    @Test
    void testDeletePerformer_NotHeadlinerOnEvent() {
        testSavePerformer_Success();
        createEventForDeleteTests();

        restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, false), EventInfo.class);

        restTemplate.delete("/api/performers/1");

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.getForEntity("/api/events/1", EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getEventName()).isEqualTo("No headliner performer yet - Budapest Park");
        assertThat(eventInfo.getPerformers()).isEmpty();
    }

    void createEventForDeleteTests() {
        restTemplate.postForEntity("/api/musicVenues",
                new MusicVenueCreateCommand("Budapest Park", "06-30-211-3221",
                        "Budapest, Soroksári út 60, 1095", 4000, BANDSTAND), MusicVenueInfo.class);
        restTemplate.postForEntity("/api/events",
                new EventCreateCommand(LocalDateTime.now().plusMonths(1), 200, 1), EventInfo.class);
    }
}
