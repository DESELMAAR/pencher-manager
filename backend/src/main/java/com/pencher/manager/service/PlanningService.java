package com.pencher.manager.service;

import com.pencher.manager.dto.PlanningRecordResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanningService {
    Optional<PlanningRecordResponse> getPlanningForUserAndDate(Long userId, LocalDate date);
    List<PlanningRecordResponse> getPlanningForEmployee(Long employeeId, LocalDate from, LocalDate to);
    void syncPlanningForEmployee(Long employeeId);
    void syncPlanningForAll();
}
