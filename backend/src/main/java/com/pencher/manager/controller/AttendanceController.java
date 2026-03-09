package com.pencher.manager.controller;

import com.pencher.manager.dto.AttendanceRecordResponse;
import com.pencher.manager.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/me/today")
    @Operation(summary = "My attendance today")
    public ResponseEntity<AttendanceRecordResponse> getMyToday() {
        return ResponseEntity.ok(attendanceService.getMyAttendanceToday());
    }

    @GetMapping("/me/history")
    @Operation(summary = "My attendance history")
    public ResponseEntity<List<AttendanceRecordResponse>> getMyHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(attendanceService.getMyAttendanceHistory(page, size));
    }

    @GetMapping("/daily")
    @Operation(summary = "Daily attendance")
    public ResponseEntity<List<AttendanceRecordResponse>> getDaily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getDailyAttendance(date));
    }

    @GetMapping("/late")
    @Operation(summary = "Late attendance")
    public ResponseEntity<List<AttendanceRecordResponse>> getLate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getLateAttendance(date));
    }

    @GetMapping("/absent")
    @Operation(summary = "Absent")
    public ResponseEntity<List<AttendanceRecordResponse>> getAbsent(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAbsentAttendance(date));
    }

    @GetMapping("/on-time")
    @Operation(summary = "On-time attendance")
    public ResponseEntity<List<AttendanceRecordResponse>> getOnTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getOnTimeAttendance(date));
    }

    @GetMapping("/summary")
    @Operation(summary = "Attendance summary")
    public ResponseEntity<Object> getSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getSummary(from, to));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Attendance by employee")
    public ResponseEntity<List<AttendanceRecordResponse>> getByEmployee(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getAttendanceByEmployee(employeeId, from, to));
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Attendance by team")
    public ResponseEntity<List<AttendanceRecordResponse>> getByTeam(
            @PathVariable Long teamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByTeam(teamId, date));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Attendance by department")
    public ResponseEntity<List<AttendanceRecordResponse>> getByDepartment(
            @PathVariable Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByDepartment(departmentId, date));
    }
}
