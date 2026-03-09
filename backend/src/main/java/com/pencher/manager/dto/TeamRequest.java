package com.pencher.manager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamRequest {
    @NotNull(message = "Department ID is required")
    private Long departmentId;
    @jakarta.validation.constraints.NotBlank(message = "Team name is required")
    private String name;
    private String description;
}
