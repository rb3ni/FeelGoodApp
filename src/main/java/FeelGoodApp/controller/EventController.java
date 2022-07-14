package FeelGoodApp.controller;

import FeelGoodApp.dto.EventInfo;
import FeelGoodApp.dto.command.AddPerformerToEventCommand;
import FeelGoodApp.dto.command.EventCreateCommand;
import FeelGoodApp.dto.command.EventDateUpdateCommand;
import FeelGoodApp.exceptionhandling.EventNotFoundException;
import FeelGoodApp.exceptionhandling.MusicVenueNotFoundException;
import FeelGoodApp.service.EventPerformerService;
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
@RequestMapping("/api/events")
@Slf4j
@Tag(name = "The controller for Events")
public class EventController {

    private final EventPerformerService eventPerformerService;

    public EventController(EventPerformerService eventPerformerService) {
        this.eventPerformerService = eventPerformerService;
    }

    @Operation(summary = "Create a new Event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Return created Event."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MusicVenueNotFoundException.class)))
            )})
    @PostMapping
    public ResponseEntity<EventInfo> saveEvent(@Valid @RequestBody EventCreateCommand command) {
        log.info("Http request, POST /api/events, body: " + command.toString());
        EventInfo saved = eventPerformerService.saveEvent(command);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "Find all active Events")
    @ApiResponse(responseCode = "200", description = "Return active Events.")
    @GetMapping
    public ResponseEntity<List<EventInfo>> getEvents() {
        log.info("Http request, GET /api/events");
        List<EventInfo> eventInfosList = eventPerformerService.getEvents();
        return new ResponseEntity<>(eventInfosList, HttpStatus.OK);
    }

    @Operation(summary = "Find an Event by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return selected Event."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventNotFoundException.class)))
            )})
    @GetMapping("/{eventId}")
    public ResponseEntity<EventInfo> getEventById(@Parameter(description = "Id of Event", example = "1")
                                                  @PathVariable Integer eventId) {
        log.info("Http request, GET /api/events/{eventId}, parameter: " + eventId);
        EventInfo event = eventPerformerService.getEventById(eventId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @Operation(summary = "Assign a Performer to an Event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Event with Performer."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventNotFoundException.class)))
            )})
    @PostMapping("/{eventId}")
    public ResponseEntity<EventInfo> addPerformerToEvent(@Parameter(description = "Id of Event", example = "1")
                                                         @PathVariable Integer eventId,
                                                         @Valid @RequestBody AddPerformerToEventCommand command) {
        log.info("Http request, POST /api/events/{eventId}, parameter: " + eventId
                + " body: " + command);
        EventInfo eventPerformerModified = eventPerformerService.addPerformerToEvent(eventId, command);
        return new ResponseEntity<>(eventPerformerModified, HttpStatus.OK);
    }

    @Operation(summary = "Remove a Performer from an Event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Remove completed"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventNotFoundException.class)))
            )})
    @DeleteMapping("/{eventId}/{performerId}")
    public ResponseEntity<Void> removePerformerFromEvent(@Parameter(description = "Id of Event", example = "1")
                                                         @PathVariable Integer eventId,
                                                         @Parameter(description = "Id of Performer", example = "1")
                                                         @PathVariable Integer performerId) {
        log.info("Http request, DELETE /api/events/{eventId}/{performerId}, parameters: event id - " + eventId
                + " performer id - " + performerId);
        eventPerformerService.removePerformerFromEvent(eventId, performerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Modify date of an Event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return modified Event."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventNotFoundException.class)))
            )})
    @PutMapping("/{eventId}")
    public ResponseEntity<EventInfo> modifyEventDate(@Parameter(description = "Id of Event", example = "1")
                                                     @PathVariable Integer eventId,
                                                     @Valid @RequestBody EventDateUpdateCommand command) {
        log.info("Http request, PUT /api/events/{eventId}, parameters: " + eventId);
        EventInfo modifyEventDate = eventPerformerService.modifyEventDate(eventId, command);
        return new ResponseEntity<>(modifyEventDate, HttpStatus.OK);
    }

    @Operation(summary = "Delete an Event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete completed"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventNotFoundException.class)))
            )})
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@Parameter(description = "Id of Event", example = "1")
                                            @PathVariable Integer eventId) {
        log.info("Http request, DELETE /api/events/{eventId}, parameter: "
                + eventId);
        eventPerformerService.deleteEvent(eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
