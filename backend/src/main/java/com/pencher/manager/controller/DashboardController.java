package com.pencher.manager.controller;

import com.pencher.manager.dto.DashboardSummary;
import com.pencher.manager.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/super-admin")
    @Operation(summary = "Super Admin dashboard")
    public ResponseEntity<DashboardSummary> superAdmin() {
        return ResponseEntity.ok(dashboardService.getSuperAdminDashboard());
    }

    @GetMapping("/department-admin")
    @Operation(summary = "Department Admin dashboard")
    public ResponseEntity<DashboardSummary> departmentAdmin() {
        return ResponseEntity.ok(dashboardService.getDepartmentAdminDashboard());
    }

    @GetMapping("/team-leader")
    @Operation(summary = "Team Leader dashboard")
    public ResponseEntity<DashboardSummary> teamLeader() {
        return ResponseEntity.ok(dashboardService.getTeamLeaderDashboard());
    }

    @GetMapping("/employee")
    @Operation(summary = "Employee dashboard")
    public ResponseEntity<DashboardSummary> employee() {
        return ResponseEntity.ok(dashboardService.getEmployeeDashboard());
    }
}