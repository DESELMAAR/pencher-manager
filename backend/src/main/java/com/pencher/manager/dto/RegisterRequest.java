package com.pencher.manager.dto;

import com.pencher.manager.entity.enums.RoleType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 6)
    private String password;
    @NotNull
    private RoleType role;
    private String employeeId;
    private String phoneNumber;
    private LocalDate hiringDate;
    private Long departmentId;
    private Long teamId;
}
