package com.pencher.manager.mapper;

import com.pencher.manager.dto.AttendanceRecordResponse;
import com.pencher.manager.entity.AttendanceRecord;
import org.springframework.stereotype.Component;

@Component
public class AttendanceRecordMapper {

    public AttendanceRecordResponse toResponse(AttendanceRecord entity) {
        if (entity == null) return null;
        String fullName = entity.getUser() != null ? entity.getUser().getFullName() : null;
        String employeeId = entity.getUser() != null ? entity.getUser().getEmployeeId() : null;
        return AttendanceRecordResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .userFullName(fullName)
                .employeeId(employeeId)
                .attendanceDate(entity.getAttendanceDate())
                .attendanceStatus(entity.getAttendanceStatus())
                .plannedStartTime(entity.getPlannedStartTime())
                .actualStartTime(entity.getActualStartTime())
                .delayMinutes(entity.getDelayMinutes())
                .build();
    }
}
