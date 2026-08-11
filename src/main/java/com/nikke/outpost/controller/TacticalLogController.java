package com.nikke.outpost.controller;

import com.nikke.outpost.dto.request.CreateTacticalLogRequest;
import com.nikke.outpost.dto.response.TacticalLogResponse;
import com.nikke.outpost.service.TacticalLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
@Tag(name = "Tactical Intelligence Logs", description = "Endpoints for appending skill mechanics, synergy guides, and " +
        "unstructured lore notes")
public class TacticalLogController {

    private final TacticalLogService tacticalLogService;

    // --------------------------------------------------------------------------------
    // POST /api/v1/units/{id}/logs
    // --------------------------------------------------------------------------------
    @PostMapping(value = "/{id}/logs", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Attach a tactical log to a unit",
            description = "Appends unstructured combat notes, skill breakdowns, or team synergy guides to a unit. " +
                    "These logs form the knowledge base for RAG vector embeddings in Phase 3."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tactical log successfully attached to the unit",
                    content = @Content(schema = @Schema(implementation = TacticalLogResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure (e.g., blank log title or empty content body)",
                    content = @Content(schema = @Schema(example = "{\"timestamp\": \"2026-08-10T21:00:00\", \"status\": 400, \"error\": \"Validation Failed\", \"fieldErrors\": {\"title\": \"Log title is required\"}}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Target unit ID does not exist",
                    content = @Content(schema = @Schema(example = "{\"timestamp\": \"2026-08-10T21:00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Cannot add log. Unit not found with id: 99\"}}"))
            )
    })    public ResponseEntity<TacticalLogResponse> addLogToUnit(
            @Parameter(
                    name = "id",
                    description = "Unique numeric identifier of the parent unit",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JSON object containing log title and detailed combat text content",
                    required = true
            )
            @Valid @RequestBody CreateTacticalLogRequest request) {

        TacticalLogResponse logResponse = tacticalLogService.addLogToUnit(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(logResponse);
    }
}
