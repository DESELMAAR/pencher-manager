package com.pencher.manager.controller;

import com.pencher.manager.dto.PlanningRecordResponse;
import com.pencher.manager.service.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/planning")
@RequiredArgsConstructor
@Tag(name = "Planning")
public class PlanningController {

    private final PlanningService planningService;

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Planning for employee")
    public ResponseEntity<List<PlanningRecordResponse>> getByEmployee(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(planningService.getPlanningForEmployee(employeeId, from, to));
    }

    @PostMapping("/sync/{employeeId}")
    @Operation(summary = "Sync planning for employee")
    public ResponseEntity<Void> syncEmployee(@PathVariable Long employeeId) {
        planningService.syncPlanningForEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sync-all")
    @Operation(summary = "Sync planning for all")
    public ResponseEntity<Void> syncAll() {
        planningService.syncPlanningForAll();
        return ResponseEntity.noContent().build();
    }
}
