package com.pencher.manager.dto;

import com.pencher.manager.entity.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AttendanceRecordResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private String employeeId;
    private LocalDate attendanceDate;
    private AttendanceStatus attendanceStatus;
    private LocalTime plannedStartTime;
    private LocalTime actualStartTime;
    private Integer delayMinutes;
}
