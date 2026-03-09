package com.pencher.manager.controller;

import com.pencher.manager.dto.DepartmentRequest;
import com.pencher.manager.dto.DepartmentResponse;
import com.pencher.manager.dto.TeamResponse;
import com.pencher.manager.service.DepartmentService;
import com.pencher.manager.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "Create department")
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @GetMapping
    @Operation(summary = "List departments")
    public ResponseEntity<List<DepartmentResponse>> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<DepartmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department")
    public ResponseEntity<DepartmentResponse> update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{departmentId}/teams")
    @Operation(summary = "List teams by department")
    public ResponseEntity<List<TeamResponse>> getTeamsByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(teamService.findByDepartmentId(departmentId));
    }
}
