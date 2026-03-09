package com.pencher.manager.mapper;

import com.pencher.manager.dto.PlanningRecordResponse;
import com.pencher.manager.entity.PlanningRecord;
import org.springframework.stereotype.Component;

@Component
public class PlanningRecordMapper {

    public PlanningRecordResponse toResponse(PlanningRecord entity, String employeeId) {
        if (entity == null) return null;
        return PlanningRecordResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .employeeId(employeeId)
                .planDate(entity.getPlanDate())
                .plannedStartTime(entity.getPlannedStartTime())
                .plannedEndTime(entity.getPlannedEndTime())
                .scheduled(entity.getScheduled())
                .shiftType(entity.getShiftType())
                .build();
    }
}
