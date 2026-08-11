package com.nikke.outpost.controller;

import com.nikke.outpost.dto.request.CreateUnitRequest;
import com.nikke.outpost.dto.response.UnitResponse;
import com.nikke.outpost.dto.response.UnitSummaryResponse;
import com.nikke.outpost.enums.BurstType;
import com.nikke.outpost.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
@Tag(name = "Unit Roster Management", description = "Endpoints for registering and querying native Nikkes and crossover"
        + " units.")
public class UnitController {

    private final UnitService unitService;

    // Register Unit
    @PostMapping
    @Operation(summary = "Register a new Combat Unit", description = "Creates a unit. External IP units automatically" +
            " enforce ABNORMAL manufacturer")
    public ResponseEntity<UnitResponse> createUnit(@Valid @RequestBody CreateUnitRequest request) {
        UnitResponse createdUnit = unitService.createUnit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUnit);
    }

    // List Units with Query Filters
    @GetMapping
    @Operation(summary = "Fetch all units", description = "Retrieve units summary list with optional query parameters "
            + "for originIp and burstTypes.")
    public ResponseEntity<List<UnitSummaryResponse>> getAllUnits(
            @RequestParam(required = false) String originIp,
            @RequestParam(required = false)BurstType burstType) {

        List<UnitSummaryResponse> units = unitService.getAllUnits(originIp, burstType);
        return ResponseEntity.ok(units);
    }

    // Fetch Unit Details with Logs
    @GetMapping("/{id}")
    @Operation(summary = "Fetch unit details by ID", description = "Retrieves detailed information for a single unit " +
            "including attached tactical logs.")
    public ResponseEntity<UnitResponse> getUnitById(@PathVariable Long id) {
        UnitResponse unit = unitService.getUnitById(id);
        return ResponseEntity.ok(unit);
    }
}
