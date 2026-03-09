package com.pencher.manager.mapper;

import com.pencher.manager.dto.TeamResponse;
import com.pencher.manager.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public TeamResponse toResponse(Team entity, String leaderUserName) {
        if (entity == null) return null;
        return TeamResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .departmentId(entity.getDepartment() != null ? entity.getDepartment().getId() : null)
                .departmentName(entity.getDepartment() != null ? entity.getDepartment().getName() : null)
                .leaderUserId(entity.getLeaderUserId())
                .leaderUserName(leaderUserName)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
