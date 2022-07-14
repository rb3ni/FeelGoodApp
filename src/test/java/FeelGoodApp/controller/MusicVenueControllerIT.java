package FeelGoodApp.controller;

import FeelGoodApp.dto.EventInfo;
import FeelGoodApp.dto.MusicVenueInfo;
import FeelGoodApp.dto.command.EventCreateCommand;
import FeelGoodApp.dto.command.MusicVenueCreateCommand;
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

import static FeelGoodApp.domain.enums.VenueType.BANDSTAND;
import static FeelGoodApp.domain.enums.VenueType.CLUB;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MusicVenueControllerIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void testSaveMusicVenue_Success() {
        ResponseEntity<MusicVenueInfo> resultResponseEntity = restTemplate.postForEntity("/api/musicVenues",
                new MusicVenueCreateCommand("Budapest Park", "06-30-211-3221",
                        "Budapest, Soroksári út 60, 1095", 4000, BANDSTAND), MusicVenueInfo.class);

        MusicVenueInfo musicVenueInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        assertThat(musicVenueInfo.getName()).isEqualTo("Budapest Park");
        assertThat(musicVenueInfo.getCapacity()).isEqualTo(4000);
        assertThat(musicVenueInfo.getType()).isEqualTo(BANDSTAND);
    }

    @Test
    void testSaveMusicVenue_AllValidation() {
        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/musicVenues",
                new MusicVenueCreateCommand("", "06-30-211-35121215515155155221",
                        "", 150, null), ValidationError[].class);

        List<String> fields = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .map(ValidationError::getField)
                .collect(Collectors.toList());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(fields).contains("name", "contactPhoneNumber", "address", "capacity", "type");
    }

    @Test
    void testSaveMusicVenue_MusicVenueNameNotUniqueException() {
        testSaveMusicVenue_Success();

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.postForEntity("/api/musicVenues",
                new MusicVenueCreateCommand("Budapest Park", "06-30-211-3221",
                        "Budapest, Soroksári út 60, 1095", 4000, BANDSTAND), ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("name");
        assertThat(validationError.getErrorMessage()).contains("MusicVenue name already exist");
    }

    @Test
    void testGetMusicVenues_Empty() {
        ResponseEntity<MusicVenueInfo[]> resultResponseEntity = restTemplate.getForEntity("/api/musicVenues", MusicVenueInfo[].class);
        List<MusicVenueInfo> musicVenueInfos = List.of(Objects.requireNonNull(resultResponseEntity.getBody()));

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(musicVenueInfos).isEmpty();
    }

    @Test
    void testGetMusicVenues_TwoMusicVenues() {
        testSaveMusicVenue_Success();

        restTemplate.postForEntity("/api/musicVenues",
                new MusicVenueCreateCommand("Dürer kert", "06-70-555-3221",
                        "Budapest, Öböl utca 1, 1117", 700, CLUB), MusicVenueInfo.class);

        ResponseEntity<MusicVenueInfo[]> resultResponseEntity = restTemplate.getForEntity("/api/musicVenues", MusicVenueInfo[].class);
        List<MusicVenueInfo> musicVenueInfos = List.of(Objects.requireNonNull(resultResponseEntity.getBody()));

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(musicVenueInfos.size()).isEqualTo(2);
    }

    @Test
    void testGetEventById_Success() {
        testSaveMusicVenue_Success();

        ResponseEntity<MusicVenueInfo> resultResponseEntity = restTemplate.getForEntity("/api/musicVenues/1", MusicVenueInfo.class);
        MusicVenueInfo musicVenueInfo = Objects.requireNonNull(resultResponseEntity.getBody());

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(musicVenueInfo.getName()).isEqualTo("Budapest Park");
    }

    @Test
    void testGetMusicVenueById_MusicVenueNotFoundException() {
        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.getForEntity("/api/musicVenues/1",
                ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("musicVenueId");
        assertThat(validationError.getErrorMessage()).contains("MusicVenue with id 1 is not found.");
    }

    @Test
    void testDeleteMusicVenue_WithoutHavingEvent() {
        testSaveMusicVenue_Success();

        restTemplate.delete("/api/musicVenues/1");

        ResponseEntity<ValidationError[]> resultResponseEntity = restTemplate.getForEntity("/api/musicVenues/1",
                ValidationError[].class);

        ValidationError validationError = Arrays.stream(Objects.requireNonNull(resultResponseEntity.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationError.getField()).contains("musicVenueId");
        assertThat(validationError.getErrorMessage()).contains("MusicVenue with id 1 is not found.");
    }

    @Test
    void testDeleteMusicVenue_WithHavingEvent() {
        testSaveMusicVenue_Success();
        restTemplate.postForEntity("/api/events",
                new EventCreateCommand(LocalDateTime.now().plusMonths(1), 200, 1), EventInfo.class);

        restTemplate.delete("/api/musicVenues/1");

        ResponseEntity<ValidationError[]> resultResponseEntityMusicVenue = restTemplate.getForEntity("/api/musicVenues/1",
                ValidationError[].class);
        ResponseEntity<ValidationError[]> resultResponseEntityEvent = restTemplate.getForEntity("/api/events/1",
                ValidationError[].class);

        ValidationError validationErrorMusicVenue = Arrays.stream(Objects.requireNonNull(resultResponseEntityMusicVenue.getBody()))
                .findFirst().get();
        ValidationError validationErrorEvent = Arrays.stream(Objects.requireNonNull(resultResponseEntityEvent.getBody()))
                .findFirst().get();

        assertThat(resultResponseEntityMusicVenue.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resultResponseEntityEvent.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationErrorMusicVenue.getField()).contains("musicVenueId");
        assertThat(validationErrorMusicVenue.getErrorMessage()).contains("MusicVenue with id 1 is not found.");
        assertThat(validationErrorEvent.getField()).contains("eventId");
        assertThat(validationErrorEvent.getErrorMessage()).contains("Event with id 1 is not found.");
    }
}
