package com.pencher.manager.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardSummary {
    private long totalDepartments;
    private long totalTeams;
    private long totalEmployees;
    private long onTimeToday;
    private long lateToday;
    private long absentToday;
    private Map<String, Long> byDepartment;
    private Map<String, Long> byStatus;
}
