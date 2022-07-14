package FeelGoodApp.controller;

import FeelGoodApp.dto.MusicVenueInfo;
import FeelGoodApp.dto.command.MusicVenueCreateCommand;
import FeelGoodApp.exceptionhandling.MusicVenueNameNotUniqueException;
import FeelGoodApp.exceptionhandling.MusicVenueNotFoundException;
import FeelGoodApp.service.MusicVenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/musicVenues")
@Slf4j
@Tag(name = "The controller for MusicVenue")
public class MusicVenueController {

    private final MusicVenueService musicVenueService;

    public MusicVenueController(MusicVenueService musicVenueService) {
        this.musicVenueService = musicVenueService;
    }

    @Operation(summary = "Create a new MusicVenue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Return created MusicVenue."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MusicVenueNameNotUniqueException.class)))
            )})
    @PostMapping
    public ResponseEntity<MusicVenueInfo> saveMusicVenue(@Valid @RequestBody MusicVenueCreateCommand command) {
        log.info("Http request, POST /api/musicVenues, body: " + command.toString());
        MusicVenueInfo saved = musicVenueService.saveMusicVenue(command);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "Find all active MusicVenues")
    @ApiResponse(responseCode = "200", description = "Return all active MusicVenues.")
    @GetMapping
    public ResponseEntity<List<MusicVenueInfo>> getMusicVenues() {
        log.info("Http request, GET /api/musicVenues");
        List<MusicVenueInfo> MusicVenueInfosList = musicVenueService.getMusicVenues();
        return new ResponseEntity<>(MusicVenueInfosList, HttpStatus.OK);
    }

    @Operation(summary = "Find an MusicVenue by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return selected MusicVenue."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MusicVenueNotFoundException.class)))
            )})
    @GetMapping("/{musicVenueId}")
    public ResponseEntity<MusicVenueInfo> getMusicVenueById(@Parameter(description = "Id of MusicVenue", example = "1")
                                                            @PathVariable Integer musicVenueId) {
        log.info("Http request, GET /api/musicVenues/{musicVenueId}, parameter: " + musicVenueId);
        MusicVenueInfo musicVenue = musicVenueService.getMusicVenueById(musicVenueId);
        return new ResponseEntity<>(musicVenue, HttpStatus.OK);
    }

    @Operation(summary = "Delete an MusicVenue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete completed"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MusicVenueNotFoundException.class)))
            )})
    @DeleteMapping("/{musicVenueId}")
    public ResponseEntity<Void> deleteMusicVenue(@Parameter(description = "Id of MusicVenue", example = "1")
                                                 @PathVariable Integer musicVenueId) {
        log.info("Http request, DELETE /api/musicVenues/{musicVenueId}, parameter: " + musicVenueId);
        musicVenueService.deleteMusicVenue(musicVenueId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
