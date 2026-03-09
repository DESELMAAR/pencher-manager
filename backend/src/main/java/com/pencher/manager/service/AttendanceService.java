package com.pencher.manager.service;

import com.pencher.manager.dto.AttendanceRecordResponse;
import com.pencher.manager.entity.enums.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    /** Compute attendance for user/date from punches and planning, persist AttendanceRecord. */
    void computeAndSaveAttendance(Long userId, LocalDate date);
    AttendanceRecordResponse getMyAttendanceToday();
    List<AttendanceRecordResponse> getMyAttendanceHistory(int page, int size);
    List<AttendanceRecordResponse> getDailyAttendance(LocalDate date);
    List<AttendanceRecordResponse> getLateAttendance(LocalDate date);
    List<AttendanceRecordResponse> getAbsentAttendance(LocalDate date);
    List<AttendanceRecordResponse> getOnTimeAttendance(LocalDate date);
    Object getSummary(LocalDate from, LocalDate to);
    List<AttendanceRecordResponse> getAttendanceByEmployee(Long employeeId, LocalDate from, LocalDate to);
    List<AttendanceRecordResponse> getAttendanceByTeam(Long teamId, LocalDate date);
    List<AttendanceRecordResponse> getAttendanceByDepartment(Long departmentId, LocalDate date);
}
