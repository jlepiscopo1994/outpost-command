package com.nikke.outpost.controller;

import com.nikke.outpost.dto.request.CreateTacticalLogRequest;
import com.nikke.outpost.dto.response.TacticalLogResponse;
import com.nikke.outpost.service.TacticalLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
@Tag(name = "Tactical Intelligence Logs", description = "Endpoints for appending skill mechanics, synergy notes, and " +
        "lore to combat units.")
public class TacticalLogController {

    private final TacticalLogService tacticalLogService;

    // Append Tactical Log
    @PostMapping("/{id}/logs")
    @Operation(summary = "Attach tactical log to a unit", description = "Appends unstructured combat notes to a specific"
            + " unit for future RAG ingestion.")
    public ResponseEntity<TacticalLogResponse> addLogToUnit(
            @PathVariable Long id,
            @Valid @RequestBody CreateTacticalLogRequest request){
        TacticalLogResponse logResponse = tacticalLogService.addLogToUnit(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(logResponse);
    }
}
