package com.pencher.manager.mapper;

import com.pencher.manager.dto.UserRequest;
import com.pencher.manager.dto.UserResponse;
import com.pencher.manager.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .status(user.getStatus())
                .employeeId(user.getEmployeeId())
                .phoneNumber(user.getPhoneNumber())
                .hiringDate(user.getHiringDate())
                .role(user.getRole())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .teamId(user.getTeam() != null ? user.getTeam().getId() : null)
                .teamName(user.getTeam() != null ? user.getTeam().getName() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }

    public void updateEntity(User user, UserRequest req) {
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getEmployeeId() != null) user.setEmployeeId(req.getEmployeeId());
        if (req.getPhoneNumber() != null) user.setPhoneNumber(req.getPhoneNumber());
        if (req.getHiringDate() != null) user.setHiringDate(req.getHiringDate());
        if (req.getRole() != null) user.setRole(req.getRole());
    }
}
