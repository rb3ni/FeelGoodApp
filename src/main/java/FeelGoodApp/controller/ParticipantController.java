package FeelGoodApp.controller;

import FeelGoodApp.dto.ParticipantInfo;
import FeelGoodApp.dto.ParticipantInfoList;
import FeelGoodApp.dto.command.ParticipantCreateCommand;
import FeelGoodApp.exceptionhandling.EventNotFoundException;
import FeelGoodApp.service.ParticipantService;
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
@RequestMapping("/api/participants")
@Slf4j
@Tag(name = "The controller for Participants")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @Operation(summary = "Create a new Participant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Return created Participant."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventNotFoundException.class)))
            )})
    @PostMapping("/{eventId}")
    public ResponseEntity<ParticipantInfo> saveParticipant(@Parameter(description = "Id of Event", example = "1")
                                                           @PathVariable Integer eventId,
                                                           @Valid @RequestBody ParticipantCreateCommand command) {
        log.info("Http request, POST /api/participants/{eventId}, parameter: " + eventId +
                " body: " + command.toString());
        ParticipantInfo saved = participantService.saveParticipant(eventId, command);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "Find all Participants on Event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return all Participants on Event."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventNotFoundException.class)))
            )})
    @GetMapping("/{eventId}")
    public ResponseEntity<List<ParticipantInfoList>> getParticipantsByEvent(
            @Parameter(description = "Id of Event", example = "1")
            @PathVariable Integer eventId) {
        log.info("Http request, GET /api/participants/{eventId}, parameter: " + eventId);
        List<ParticipantInfoList> participants = participantService.getParticipantsByEvent(eventId);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }
}
