package com.pencher.manager.dto;

import com.pencher.manager.entity.enums.RoleType;
import com.pencher.manager.entity.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private UserStatus status;
    private String employeeId;
    private String phoneNumber;
    private LocalDate hiringDate;
    private RoleType role;
    private Long departmentId;
    private String departmentName;
    private Long teamId;
    private String teamName;
    private Instant createdAt;
}
