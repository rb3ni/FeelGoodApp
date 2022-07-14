package FeelGoodApp.controller;

import FeelGoodApp.dto.PerformerInfo;
import FeelGoodApp.dto.command.PerformerCreateCommand;
import FeelGoodApp.dto.command.PerformerPartnerLevelUpdateCommand;
import FeelGoodApp.exceptionhandling.PerformerNameNotUniqueException;
import FeelGoodApp.exceptionhandling.PerformerNotFoundException;
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
@RequestMapping("/api/performers")
@Slf4j
@Tag(name = "The controller for Performers")
public class PerformerController {

    private final EventPerformerService eventPerformerService;

    public PerformerController(EventPerformerService eventPerformerService) {
        this.eventPerformerService = eventPerformerService;
    }

    @Operation(summary = "Create a new Performer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Return created Performer."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PerformerNameNotUniqueException.class)))
            )})
    @PostMapping
    public ResponseEntity<PerformerInfo> savePerformer(@Valid @RequestBody PerformerCreateCommand command) {
        log.info("Http request, POST /api/performers, body: " + command.toString());
        PerformerInfo saved = eventPerformerService.savePerformer(command);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "Find all active Performers")
    @ApiResponse(responseCode = "200", description = "Return active Performers.")
    @GetMapping
    public ResponseEntity<List<PerformerInfo>> getPerformers() {
        log.info("Http request, GET /api/performers");
        List<PerformerInfo> performerInfos = eventPerformerService.getPerformers();
        return new ResponseEntity<>(performerInfos, HttpStatus.OK);
    }

    @Operation(summary = "Find an Performer by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return selected Performer."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PerformerNotFoundException.class)))
            )})
    @GetMapping("/{performerId}")
    public ResponseEntity<PerformerInfo> getPerformerById(@Parameter(description = "Id of Performer", example = "1")
                                                          @PathVariable Integer performerId) {
        log.info("Http request, GET /api/performers/{performerId}, parameter: " + performerId);
        PerformerInfo performer = eventPerformerService.getPerformerById(performerId);
        return new ResponseEntity<>(performer, HttpStatus.OK);
    }

    @Operation(summary = "Modify partner level of Performer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return modified Performer."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PerformerNotFoundException.class)))
            )})
    @PutMapping("/{performerId}")
    public ResponseEntity<PerformerInfo> modifyPerformerTier(@Parameter(description = "Id of Performer", example = "1")
                                                             @PathVariable Integer performerId,
                                                             @Valid @RequestBody PerformerPartnerLevelUpdateCommand command) {
        log.info("Http request, PUT /api/performers/{performerId}, parameters: " + performerId);
        PerformerInfo modifyPerformerTier = eventPerformerService.modifyPerformerTier(performerId, command);
        return new ResponseEntity<>(modifyPerformerTier, HttpStatus.OK);
    }

    @Operation(summary = "Delete an Performer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete completed"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request!",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PerformerNotFoundException.class)))
            )})
    @DeleteMapping("/{performerId}")
    public ResponseEntity<Void> deletePerformer(@Parameter(description = "Id of Performer", example = "1")
                                                @PathVariable Integer performerId) {
        log.info("Http request, DELETE /api/performers/{performerId}, parameter: "
                + performerId);
        eventPerformerService.deletePerformer(performerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
