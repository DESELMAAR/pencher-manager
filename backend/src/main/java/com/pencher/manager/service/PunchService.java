package com.pencher.manager.service;

import com.pencher.manager.dto.PunchEventResponse;
import com.pencher.manager.entity.enums.PunchType;

import java.time.LocalDate;
import java.util.List;

public interface PunchService {
    PunchEventResponse punch(PunchType type);
    List<PunchEventResponse> getMyPunchesToday();
    List<PunchEventResponse> getMyPunchesHistory(int page, int size);
    List<PunchEventResponse> getPunchesByEmployee(Long employeeId, LocalDate from, LocalDate to);
    List<PunchEventResponse> getPunchesByTeam(Long teamId, LocalDate date);
    List<PunchEventResponse> getPunchesByDepartment(Long departmentId, LocalDate date);
}
