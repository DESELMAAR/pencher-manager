package com.pencher.manager.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DepartmentResponse {
    private Long id;
    private String name;
    private String description;
    private Long adminUserId;
    private String adminUserName;
    private Instant createdAt;
    private Instant updatedAt;
}
