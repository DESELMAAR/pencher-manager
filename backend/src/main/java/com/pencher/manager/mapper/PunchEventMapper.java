package com.pencher.manager.mapper;

import com.pencher.manager.dto.PunchEventResponse;
import com.pencher.manager.entity.PunchEvent;
import org.springframework.stereotype.Component;

@Component
public class PunchEventMapper {

    public PunchEventResponse toResponse(PunchEvent entity) {
        if (entity == null) return null;
        return PunchEventResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .punchType(entity.getPunchType())
                .punchAt(entity.getPunchAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
