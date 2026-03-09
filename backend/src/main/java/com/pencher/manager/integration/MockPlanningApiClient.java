package com.pencher.manager.integration;

import com.pencher.manager.integration.dto.PlanningApiResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation: returns default 9-17 schedule for weekdays.
 * Replace with RealPlanningApiClient when external API is available.
 */
public class MockPlanningApiClient implements PlanningApiClient {

    @Override
    public List<PlanningApiResponse> getPlanningForEmployee(String employeeId, LocalDate from, LocalDate to) {
        List<PlanningApiResponse> list = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            if (d.getDayOfWeek().getValue() < 6) { // Mon-Fri
                PlanningApiResponse r = new PlanningApiResponse();
                r.setEmployeeId(employeeId);
                r.setDate(d);
                r.setPlannedStartTime(LocalTime.of(9, 0));
                r.setPlannedEndTime(LocalTime.of(17, 0));
                r.setScheduled(true);
                r.setShiftType("DAY");
                list.add(r);
            }
        }
        return list;
    }
}
