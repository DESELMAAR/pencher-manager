package com.pencher.manager.mapper;

import com.pencher.manager.dto.DepartmentResponse;
import com.pencher.manager.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse(Department entity, String adminUserName) {
        if (entity == null) return null;
        return DepartmentResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .adminUserId(entity.getAdminUserId())
                .adminUserName(adminUserName)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
