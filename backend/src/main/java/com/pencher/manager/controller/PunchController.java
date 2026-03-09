package com.pencher.manager.controller;

import com.pencher.manager.dto.PunchEventResponse;
import com.pencher.manager.entity.enums.PunchType;
import com.pencher.manager.service.PunchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/punches")
@RequiredArgsConstructor
@Tag(name = "Punches")
public class PunchController {

    private final PunchService punchService;

    @PostMapping("/start-work")
    @Operation(summary = "Punch start work")
    public ResponseEntity<PunchEventResponse> startWork() {
        return ResponseEntity.status(HttpStatus.CREATED).body(punchService.punch(PunchType.START_WORK));
    }

    @PostMapping("/break-1")
    @Operation(summary = "Punch break 1")
    public ResponseEntity<PunchEventResponse> break1() {
        return ResponseEntity.status(HttpStatus.CREATED).body(punchService.punch(PunchType.BREAK_1));
    }

    @PostMapping("/break-2")
    @Operation(summary = "Punch break 2")
    public ResponseEntity<PunchEventResponse> break2() {
        return ResponseEntity.status(HttpStatus.CREATED).body(punchService.punch(PunchType.BREAK_2));
    }

    @PostMapping("/lunch-break")
    @Operation(summary = "Punch lunch break")
    public ResponseEntity<PunchEventResponse> lunchBreak() {
        return ResponseEntity.status(HttpStatus.CREATED).body(punchService.punch(PunchType.LUNCH_BREAK));
    }

    @PostMapping("/end-shift")
    @Operation(summary = "Punch end shift")
    public ResponseEntity<PunchEventResponse> endShift() {
        return ResponseEntity.status(HttpStatus.CREATED).body(punchService.punch(PunchType.END_SHIFT));
    }

    @GetMapping("/me/today")
    @Operation(summary = "My punches today")
    public ResponseEntity<List<PunchEventResponse>> getMyPunchesToday() {
        return ResponseEntity.ok(punchService.getMyPunchesToday());
    }

    @GetMapping("/me/history")
    @Operation(summary = "My punch history")
    public ResponseEntity<List<PunchEventResponse>> getMyPunchesHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(punchService.getMyPunchesHistory(page, size));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Punches by employee")
    public ResponseEntity<List<PunchEventResponse>> getByEmployee(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(punchService.getPunchesByEmployee(employeeId, from, to));
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Punches by team for date")
    public ResponseEntity<List<PunchEventResponse>> getByTeam(
            @PathVariable Long teamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(punchService.getPunchesByTeam(teamId, date));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Punches by department for date")
    public ResponseEntity<List<PunchEventResponse>> getByDepartment(
            @PathVariable Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(punchService.getPunchesByDepartment(departmentId, date));
    }
}
