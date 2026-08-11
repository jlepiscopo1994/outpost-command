package com.nikke.outpost.controller;

import com.nikke.outpost.dto.request.CreateUnitRequest;
import com.nikke.outpost.dto.response.UnitResponse;
import com.nikke.outpost.dto.response.UnitSummaryResponse;
import com.nikke.outpost.enums.BurstType;
import com.nikke.outpost.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
@Tag(name = "Unit Roster Management", description = "Endpoints for registering, filtering, and retrieving combat units (Nikkes & Crossovers)")
public class UnitController {

    private final UnitService unitService;

    // --------------------------------------------------------------------------------
    // POST /api/v1/units
    // --------------------------------------------------------------------------------
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Register a new Combat Unit",
            description = "Creates a new combat unit. If originIp is anything other than 'Nikke' (e.g. 'NieR:Automata'), " +
                    "the service automatically enforces the 'ABNORMAL' manufacturer archetype."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Unit created successfully",
                    content = @Content(schema = @Schema(implementation = UnitResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure (e.g., missing mandatory attributes or invalid field lengths)",
                    content = @Content(schema = @Schema(example = "{\"timestamp\": \"2026-08-10T21:00:00\", \"status\": 400, \"error\": \"Validation Failed\", \"fieldErrors\": {\"name\": \"Unit name is required\"}}"))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - A unit with the specified name already exists",
                    content = @Content(schema = @Schema(example = "{\"timestamp\": \"2026-08-10T21:00:00\", \"status\": 409, \"error\": \"Conflict\", \"message\": \"Unit with name 'Rapi' already exists.\"}}"))
            )
    })
    public ResponseEntity<UnitResponse> createUnit(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JSON payload containing unit attributes and gameplay classification",
                    required = true
            )
            @Valid @RequestBody CreateUnitRequest request) {
        UnitResponse createdUnit = unitService.createUnit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUnit);
    }

    // --------------------------------------------------------------------------------
    // GET /api/v1/units
    // --------------------------------------------------------------------------------
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Fetch unit roster list",
            description = "Retrieves a summary list of registered units. Supports filtering by originating intellectual property and burst skill type."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of unit summaries retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UnitSummaryResponse.class)))
            )
    })    public ResponseEntity<List<UnitSummaryResponse>> getAllUnits(
            @Parameter(
                    name = "originIp",
                    description = "Filter units by originating IP (e.g., 'Nikke', 'NieR:Automata', 'Stellar Blade')",
                    example = "NieR:Automata",
                    required = false
            )
            @RequestParam(required = false) String originIp,

            @Parameter(
                    name = "burstType",
                    description = "Filter units by squad burst skill tier (BURST_1, BURST_2, BURST_3)",
                    example = "BURST_3",
                    required = false
            )
            @RequestParam(required = false) BurstType burstType) {

        List<UnitSummaryResponse> units = unitService.getAllUnits(originIp, burstType);
        return ResponseEntity.ok(units);
    }

    // --------------------------------------------------------------------------------
    // GET /api/v1/units/{id}
    // --------------------------------------------------------------------------------
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Fetch unit details by ID",
            description = "Retrieves full details for a specific unit, including all attached tactical logs and combat intelligence records."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Unit details found and returned",
                    content = @Content(schema = @Schema(implementation = UnitResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Unit not found with the requested ID",
                    content = @Content(schema = @Schema(example = "{\"timestamp\": \"2026-08-10T21:00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Unit not found with id: 99\"}}"))
            )
    })  public ResponseEntity<UnitResponse> getUnitById(
            @Parameter(
                    name = "id",
                    description = "Unique numeric identifier of the combat unit",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        UnitResponse unit = unitService.getUnitById(id);
        return ResponseEntity.ok(unit);
    }
}
