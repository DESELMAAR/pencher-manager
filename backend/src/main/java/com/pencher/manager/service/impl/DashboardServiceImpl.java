package com.pencher.manager.service.impl;

import com.pencher.manager.dto.DashboardSummary;
import com.pencher.manager.entity.enums.AttendanceStatus;
import com.pencher.manager.entity.enums.RoleType;
import com.pencher.manager.repository.AttendanceRecordRepository;
import com.pencher.manager.repository.DepartmentRepository;
import com.pencher.manager.repository.TeamRepository;
import com.pencher.manager.repository.UserRepository;
import com.pencher.manager.security.CurrentUser;
import com.pencher.manager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummary getSuperAdminDashboard() {
        if (!CurrentUser.isSuperAdmin()) throw new com.pencher.manager.exception.ForbiddenException("Super Admin only");
        LocalDate today = LocalDate.now();
        long onTime = attendanceRecordRepository.countByAttendanceDateAndAttendanceStatus(today, AttendanceStatus.ON_TIME);
        long late = attendanceRecordRepository.countByAttendanceDateAndAttendanceStatus(today, AttendanceStatus.LATE);
        long absent = attendanceRecordRepository.countByAttendanceDateAndAttendanceStatus(today, AttendanceStatus.ABSENT);
        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("ON_TIME", onTime);
        byStatus.put("LATE", late);
        byStatus.put("ABSENT", absent);
        return DashboardSummary.builder()
                .totalDepartments(departmentRepository.count())
                .totalTeams(teamRepository.count())
                .totalEmployees(userRepository.count())
                .onTimeToday(onTime)
                .lateToday(late)
                .absentToday(absent)
                .byStatus(byStatus)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummary getDepartmentAdminDashboard() {
        Long userId = CurrentUser.getIdOrThrow();
        var me = userRepository.findById(userId).orElseThrow();
        if (me.getDepartment() == null) return emptySummary();
        Long deptId = me.getDepartment().getId();
        LocalDate today = LocalDate.now();
        var records = attendanceRecordRepository.findByDepartmentIdAndDate(deptId, today);
        long onTime = records.stream().filter(r -> r.getAttendanceStatus() == AttendanceStatus.ON_TIME).count();
        long late = records.stream().filter(r -> r.getAttendanceStatus() == AttendanceStatus.LATE).count();
        long absent = records.stream().filter(r -> r.getAttendanceStatus() == AttendanceStatus.ABSENT).count();
        long teams = teamRepository.findByDepartmentId(deptId).size();
        long employees = userRepository.findByDepartmentId(deptId).size();
        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("ON_TIME", onTime);
        byStatus.put("LATE", late);
        byStatus.put("ABSENT", absent);
        return DashboardSummary.builder()
                .totalDepartments(1)
                .totalTeams(teams)
                .totalEmployees(employees)
                .onTimeToday(onTime)
                .lateToday(late)
                .absentToday(absent)
                .byStatus(byStatus)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummary getTeamLeaderDashboard() {
        Long userId = CurrentUser.getIdOrThrow();
        var me = userRepository.findById(userId).orElseThrow();
        if (me.getTeam() == null) return emptySummary();
        Long teamId = me.getTeam().getId();
        LocalDate today = LocalDate.now();
        var records = attendanceRecordRepository.findByTeamIdAndDate(teamId, today);
        long onTime = records.stream().filter(r -> r.getAttendanceStatus() == AttendanceStatus.ON_TIME).count();
        long late = records.stream().filter(r -> r.getAttendanceStatus() == AttendanceStatus.LATE).count();
        long absent = records.stream().filter(r -> r.getAttendanceStatus() == AttendanceStatus.ABSENT).count();
        long employees = userRepository.findByTeamId(teamId).size();
        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("ON_TIME", onTime);
        byStatus.put("LATE", late);
        byStatus.put("ABSENT", absent);
        return DashboardSummary.builder()
                .totalDepartments(1)
                .totalTeams(1)
                .totalEmployees(employees)
                .onTimeToday(onTime)
                .lateToday(late)
                .absentToday(absent)
                .byStatus(byStatus)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummary getEmployeeDashboard() {
        Long userId = CurrentUser.getIdOrThrow();
        LocalDate today = LocalDate.now();
        var opt = attendanceRecordRepository.findByUserIdAndAttendanceDate(userId, today);
        long onTime = opt.map(r -> r.getAttendanceStatus() == AttendanceStatus.ON_TIME ? 1L : 0L).orElse(0L);
        long late = opt.map(r -> r.getAttendanceStatus() == AttendanceStatus.LATE ? 1L : 0L).orElse(0L);
        long absent = opt.map(r -> r.getAttendanceStatus() == AttendanceStatus.ABSENT ? 1L : 0L).orElse(0L);
        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("ON_TIME", onTime);
        byStatus.put("LATE", late);
        byStatus.put("ABSENT", absent);
        return DashboardSummary.builder()
                .totalDepartments(0)
                .totalTeams(0)
                .totalEmployees(1)
                .onTimeToday(onTime)
                .lateToday(late)
                .absentToday(absent)
                .byStatus(byStatus)
                .build();
    }

    private DashboardSummary emptySummary() {
        return DashboardSummary.builder()
                .totalDepartments(0)
                .totalTeams(0)
                .totalEmployees(0)
                .onTimeToday(0)
                .lateToday(0)
                .absentToday(0)
                .build();
    }
}
