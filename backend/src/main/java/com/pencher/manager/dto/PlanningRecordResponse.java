package com.pencher.manager.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class PlanningRecordResponse {
    private Long id;
    private Long userId;
    private String employeeId;
    private LocalDate planDate;
    private LocalTime plannedStartTime;
    private LocalTime plannedEndTime;
    private Boolean scheduled;
    private String shiftType;
}
