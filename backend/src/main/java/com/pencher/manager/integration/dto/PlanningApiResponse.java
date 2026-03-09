package com.pencher.manager.integration.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PlanningApiResponse {
    private String employeeId;
    private LocalDate date;
    private LocalTime plannedStartTime;
    private LocalTime plannedEndTime;
    private Boolean scheduled;
    private String shiftType;
}
