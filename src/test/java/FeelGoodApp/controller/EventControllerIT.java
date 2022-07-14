package FeelGoodApp.controller;

import FeelGoodApp.dto.EventInfo;
import FeelGoodApp.dto.MusicVenueInfo;
import FeelGoodApp.dto.PerformerInfo;
import FeelGoodApp.dto.command.*;
import FeelGoodApp.exceptionhandling.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static FeelGoodApp.domain.enums.GenreType.POST_ROCK;
import static FeelGoodApp.domain.enums.GenreType.ROCK;
import static FeelGoodApp.domain.enums.PartnerLevel.*;
import static FeelGoodApp.domain.enums.VenueType.BANDSTAND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EventControllerIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void testSaveEvent_Success() {
        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.postForEntity("/api/events",
                new EventCreateCommand(LocalDateTime.now().plusMonths(1), 200, 1), EventInfo.class);

        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        assertThat(eventInfo.getEventName()).isEqualTo("No headliner performer yet - Budapest Park");
        assertThat(eventInfo.getMusicVenue().getName()).isEqualTo("Budapest Park");
        assertFalse(eventInfo.getAvailableForPublic());
    }

    @Test
    void testSaveEvent_AllValidation() {
        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/events",
                new EventCreateCommand(LocalDateTime.now().minusDays(1), 250,
                        null), ValidationError[].class);

        List<String> fields = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .map(ValidationError::getField)
                .collect(Collectors.toList());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(fields).contains("eventDate", "ticketCounter", "musicVenueId");
    }

    @Test
    void testGetEvents_Empty() {
        ResponseEntity<EventInfo[]> resultResponseEntity = restTemplate.getForEntity("/api/events", EventInfo[].class);
        List<EventInfo> eventInfos = List.of(Objects.requireNonNull(resultResponseEntity.getBody()));

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfos).isEmpty();
    }

    @Test
    void testGetEvents_TwoEvents() {
        testSaveEvent_Success();
        testSaveEvent_Success();

        ResponseEntity<EventInfo[]> resultResponseEntity = restTemplate.getForEntity("/api/events", EventInfo[].class);
        List<EventInfo> eventInfos = List.of(Objects.requireNonNull(resultResponseEntity.getBody()));

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfos.size()).isEqualTo(2);
    }

    @Test
    void testGetEventById_Success() {
        testSaveEvent_Success();

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.getForEntity("/api/events/1", EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getEventName()).isEqualTo("No headliner performer yet - Budapest Park");
    }

    @Test
    void testGetEventById_EventNotFoundException() {
        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.getForEntity("/api/events/1",
                ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("eventId");
        assertThat(validationError.getErrorMessage()).contains("Event with id 1 is not found.");
    }

    @Test
    void addPerformerToEvent_Headliner_TIER1_Success() {
        testSaveEvent_Success();

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getEventName()).isEqualTo("Képzelt Város - Budapest Park");
        assertTrue(eventInfo.getAvailableForPublic());
        assertThat(eventInfo.getEventPrice()).isEqualTo(3000.0);
    }

    @Test
    void addPerformerToEvent_Headliner_TIER_2_Success() {
        testSaveEvent_Success();
        restTemplate.put("/api/performers/1", new PerformerPartnerLevelUpdateCommand(TIER_2), Void.class);

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getEventPrice()).isEqualTo(5500.0);
    }

    @Test
    void addPerformerToEvent_Headliner_TIER_3_Success() {
        testSaveEvent_Success();
        restTemplate.put("/api/performers/1", new PerformerPartnerLevelUpdateCommand(TIER_3), Void.class);

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getEventPrice()).isEqualTo(7500.0);
    }

    @Test
    void addPerformerToEvent_Headliner_TIER_4_Success() {
        testSaveEvent_Success();
        restTemplate.put("/api/performers/1", new PerformerPartnerLevelUpdateCommand(TIER_4), Void.class);

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getEventPrice()).isEqualTo(9500.0);
    }

    @Test
    void addPerformerToEvent_Headliner_TIER_5_Success() {
        testSaveEvent_Success();
        restTemplate.put("/api/performers/1", new PerformerPartnerLevelUpdateCommand(TIER_5), Void.class);

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getEventPrice()).isEqualTo(13000.0);
    }

    @Test
    void addPerformerToEvent_NotHeadliner_Success() {
        testSaveEvent_Success();

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, false), EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getEventName()).isEqualTo("No headliner performer yet - Budapest Park");
        assertFalse(eventInfo.getAvailableForPublic());
        assertThat(eventInfo.getEventPrice()).isEqualTo(0.0);
    }

    @Test
    void addPerformerToEvent_HasSamePerformerException() {
        testSaveEvent_Success();
        restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, false), EventInfo.class);

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, false), ValidationError[].class);
        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("performerId");
        assertThat(validationError.getErrorMessage()).contains("Performer with id 1 is already registered " +
                "for this event.");
    }

    @Test
    void addPerformerToEvent_EventAlreadyHasHeadlinerException() {
        testSaveEvent_Success();
        restTemplate.postForEntity("/api/performers",
                new PerformerCreateCommand("Yara", "yara@gmail.com",
                        "06-30-211-5555", ROCK, TIER_5), PerformerInfo.class);

        restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), EventInfo.class);

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(2, true), ValidationError[].class);
        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("eventId");
        assertThat(validationError.getErrorMessage()).contains("Event with id 1 has already a headliner performer.");
    }

    @Test
    void addPerformerToEvent_PastEventDateException() {
        testSaveEvent_Success();
        restTemplate.put("/api/events/1", new EventDateUpdateCommand(LocalDateTime.now().minusDays(1)), Void.class);

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/events/1",
                new AddPerformerToEventCommand(1, true), ValidationError[].class);
        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("eventId");
        assertThat(validationError.getErrorMessage()).contains("Event with id 1 is a past event.");
    }

    @Test
    void testRemovePerformerFromEvent_Success() {
        addPerformerToEvent_NotHeadliner_Success();

        restTemplate.delete("/api/events/1/1");

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.getForEntity("/api/events/1", EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getPerformers()).isEmpty();
    }

    @Test
    void testRemovePerformerFromEvent_PerformerFromEventNotFoundException() {
        addPerformerToEvent_Headliner_TIER1_Success();

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.exchange("/api/events/1/2",
                HttpMethod.DELETE, null, ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("performerId");
        assertThat(validationError.getErrorMessage()).contains("Performer with id 2 not found on event with id 1.");
    }

    @Test
    void testRemovePerformerFromEvent_HeadlinerRemoveException() {
        addPerformerToEvent_Headliner_TIER1_Success();

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.exchange("/api/events/1/1",
                HttpMethod.DELETE, null, ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("eventId, performerId");
        assertThat(validationError.getErrorMessage()).contains("Performer with id 1 is a headliner on event " +
                "with id 1. Modify the event date or delete the event.");
    }

    @Test
    void testRemovePerformerFromEvent_PastEventDateException() {
        testSaveEvent_Success();
        restTemplate.put("/api/events/1", new EventDateUpdateCommand(LocalDateTime.now().minusDays(1)), Void.class);

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.exchange("/api/events/1/1",
                HttpMethod.DELETE, null, ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("eventId");
        assertThat(validationError.getErrorMessage()).contains("Event with id 1 is a past event.");
    }

    @Test
    void testModifyEventDate_Success() {
        testSaveEvent_Success();

        restTemplate.put("/api/events/1", new EventDateUpdateCommand(LocalDateTime.of(2030, Month.AUGUST,
                30, 18, 0, 0, 0)), Void.class);

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.getForEntity("/api/events/1", EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getEventDate().getYear()).isNotEqualTo(LocalDateTime.now().getYear());
        assertThat(eventInfo.getEventDate()).isEqualTo(LocalDateTime.of(2030, Month.AUGUST, 30,
                18, 0, 0, 0));
    }

    @Test
    void testModifyEventDate_NullDateValidation() {
        testSaveEvent_Success();

        restTemplate.put("/api/events/1", new EventDateUpdateCommand(LocalDateTime.of(2030, Month.AUGUST,
                18, 18, 0, 0, 0)), Void.class);
        restTemplate.put("/api/events/1", new EventDateUpdateCommand(null), Void.class);

        ResponseEntity<EventInfo> resultResponseEntity = restTemplate.getForEntity("/api/events/1", EventInfo.class);
        EventInfo eventInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventInfo.getEventDate()).isEqualTo(LocalDateTime.of(2030, Month.AUGUST, 18,
                18, 0, 0, 0));
    }

    @Test
    void testDeleteEvent_Success() {
        testSaveEvent_Success();

        restTemplate.delete("/api/events/1");

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.getForEntity("/api/events/1",
                ValidationError[].class);

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
    }
}
