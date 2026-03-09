package com.pencher.manager.integration;

import com.pencher.manager.integration.dto.PlanningApiResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Contract for fetching employee planning from external API.
 * Implementations: real REST client or mock.
 */
public interface PlanningApiClient {
    List<PlanningApiResponse> getPlanningForEmployee(String employeeId, LocalDate from, LocalDate to);
}
