package com.pencher.manager.service;

import com.pencher.manager.dto.DashboardSummary;

public interface DashboardService {
    DashboardSummary getSuperAdminDashboard();
    DashboardSummary getDepartmentAdminDashboard();
    DashboardSummary getTeamLeaderDashboard();
    DashboardSummary getEmployeeDashboard();
}
