package com.pencher.manager.controller;

import com.pencher.manager.dto.TeamRequest;
import com.pencher.manager.dto.TeamResponse;
import com.pencher.manager.dto.UserResponse;
import com.pencher.manager.service.TeamService;
import com.pencher.manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "Teams")
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create team")
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(request));
    }

    @GetMapping
    @Operation(summary = "List teams")
    public ResponseEntity<List<TeamResponse>> findAll() {
        return ResponseEntity.ok(teamService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID")
    public ResponseEntity<TeamResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update team")
    public ResponseEntity<TeamResponse> update(@PathVariable Long id, @Valid @RequestBody TeamRequest request) {
        return ResponseEntity.ok(teamService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete team")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teamId}/employees")
    @Operation(summary = "List employees by team")
    public ResponseEntity<List<UserResponse>> getEmployeesByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(userService.findEmployeesByTeamId(teamId));
    }
}
