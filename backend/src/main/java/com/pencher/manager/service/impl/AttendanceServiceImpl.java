package com.pencher.manager.service.impl;

import com.pencher.manager.dto.AttendanceRecordResponse;
import com.pencher.manager.entity.AttendanceRecord;
import com.pencher.manager.entity.PunchEvent;
import com.pencher.manager.entity.User;
import com.pencher.manager.entity.enums.AttendanceStatus;
import com.pencher.manager.entity.enums.PunchType;
import com.pencher.manager.exception.ForbiddenException;
import com.pencher.manager.mapper.AttendanceRecordMapper;
import com.pencher.manager.repository.AttendanceRecordRepository;
import com.pencher.manager.repository.PunchEventRepository;
import com.pencher.manager.repository.UserRepository;
import com.pencher.manager.security.CurrentUser;
import com.pencher.manager.service.AttendanceService;
import com.pencher.manager.service.PlanningService;
import com.pencher.manager.service.TeamService;
import com.pencher.manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Attendance status: compare actual START_WORK punch with planned start time.
 * Grace period (minutes): within grace period after planned start = ON_TIME.
 */
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final PunchEventRepository punchEventRepository;
    private final UserRepository userRepository;
    private final AttendanceRecordMapper attendanceRecordMapper;
    private final PlanningService planningService;
    private final UserService userService;
    private final TeamService teamService;

    @Value("${pencher.attendance.grace-period-minutes:5}")
    private int gracePeriodMinutes;

    @Override
    @Transactional
    public void computeAndSaveAttendance(Long userId, LocalDate date) {
        User user = userService.getUserEntity(userId);
        Optional<com.pencher.manager.dto.PlanningRecordResponse> planning = planningService.getPlanningForUserAndDate(userId, date);
        ZoneId zone = ZoneId.systemDefault();
        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();
        Optional<PunchEvent> startPunch = punchEventRepository.findFirstByUserIdAndPunchTypeAndDate(userId, PunchType.START_WORK, dayStart, dayEnd);

        AttendanceStatus status;
        LocalTime plannedStart = null;
        LocalTime actualStart = null;
        Integer delayMinutes = null;

        if (planning.isPresent() && Boolean.TRUE.equals(planning.get().getScheduled())) {
            plannedStart = planning.get().getPlannedStartTime();
            if (startPunch.isPresent()) {
                actualStart = LocalTime.from(startPunch.get().getPunchAt().atZone(ZoneId.systemDefault()));
                long delay = ChronoUnit.MINUTES.between(plannedStart, actualStart);
                if (delay <= gracePeriodMinutes && delay >= 0)
                    status = AttendanceStatus.ON_TIME;
                else if (delay > gracePeriodMinutes)
                    status = AttendanceStatus.LATE;
                else
                    status = AttendanceStatus.ON_TIME; // early = on time
                delayMinutes = delay > 0 ? (int) delay : 0;
            } else {
                status = AttendanceStatus.ABSENT;
            }
        } else {
            if (startPunch.isPresent()) {
                actualStart = LocalTime.from(startPunch.get().getPunchAt().atZone(ZoneId.systemDefault()));
                status = AttendanceStatus.ON_TIME;
            } else {
                status = AttendanceStatus.ABSENT;
            }
        }

        AttendanceRecord record = attendanceRecordRepository.findByUserIdAndAttendanceDate(userId, date)
                .orElse(new AttendanceRecord());
        record.setUser(user);
        record.setAttendanceDate(date);
        record.setAttendanceStatus(status);
        record.setPlannedStartTime(plannedStart);
        record.setActualStartTime(actualStart);
        record.setDelayMinutes(delayMinutes);
        attendanceRecordRepository.save(record);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceRecordResponse getMyAttendanceToday() {
        Long userId = CurrentUser.getIdOrThrow();
        LocalDate today = LocalDate.now();
        return attendanceRecordRepository.findByUserIdAndAttendanceDate(userId, today)
                .map(attendanceRecordMapper::toResponse)
                .orElse(AttendanceRecordResponse.builder()
                        .userId(userId)
                        .attendanceDate(today)
                        .attendanceStatus(AttendanceStatus.ABSENT)
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getMyAttendanceHistory(int page, int size) {
        Long userId = CurrentUser.getIdOrThrow();
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(90);
        return attendanceRecordRepository.findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(userId, start, end, PageRequest.of(page, size))
                .stream().map(attendanceRecordMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getDailyAttendance(LocalDate date) {
        ensureCanViewAttendance();
        return attendanceRecordRepository.findByAttendanceDate(date).stream()
                .map(attendanceRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getLateAttendance(LocalDate date) {
        ensureCanViewAttendance();
        return attendanceRecordRepository.findByAttendanceDateAndAttendanceStatus(date, AttendanceStatus.LATE).stream()
                .map(attendanceRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getAbsentAttendance(LocalDate date) {
        ensureCanViewAttendance();
        return attendanceRecordRepository.findByAttendanceDateAndAttendanceStatus(date, AttendanceStatus.ABSENT).stream()
                .map(attendanceRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getOnTimeAttendance(LocalDate date) {
        ensureCanViewAttendance();
        return attendanceRecordRepository.findByAttendanceDateAndAttendanceStatus(date, AttendanceStatus.ON_TIME).stream()
                .map(attendanceRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Object getSummary(LocalDate from, LocalDate to) {
        ensureCanViewAttendance();
        long onTime = 0, late = 0, absent = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            onTime += attendanceRecordRepository.countByAttendanceDateAndAttendanceStatus(d, AttendanceStatus.ON_TIME);
            late += attendanceRecordRepository.countByAttendanceDateAndAttendanceStatus(d, AttendanceStatus.LATE);
            absent += attendanceRecordRepository.countByAttendanceDateAndAttendanceStatus(d, AttendanceStatus.ABSENT);
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("from", from);
        summary.put("to", to);
        summary.put("onTime", onTime);
        summary.put("late", late);
        summary.put("absent", absent);
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getAttendanceByEmployee(Long employeeId, LocalDate from, LocalDate to) {
        ensureCanViewEmployeeAttendance(employeeId);
        List<AttendanceRecord> list = attendanceRecordRepository.findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                employeeId, from, to, PageRequest.of(0, 500));
        return list.stream().map(attendanceRecordMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getAttendanceByTeam(Long teamId, LocalDate date) {
        teamService.getTeamEntity(teamId);
        ensureCanViewTeam(teamId);
        return attendanceRecordRepository.findByTeamIdAndDate(teamId, date).stream()
                .map(attendanceRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getAttendanceByDepartment(Long departmentId, LocalDate date) {
        ensureDepartmentAccess(departmentId);
        return attendanceRecordRepository.findByDepartmentIdAndDate(departmentId, date).stream()
                .map(attendanceRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void ensureCanViewAttendance() {
        if (CurrentUser.isSuperAdmin()) return;
        if (CurrentUser.getRole().orElse(null) == com.pencher.manager.entity.enums.RoleType.DEPARTMENT_ADMIN) return;
        if (CurrentUser.getRole().orElse(null) == com.pencher.manager.entity.enums.RoleType.TEAM_LEADER) return;
        throw new ForbiddenException("Cannot view attendance reports");
    }

    private void ensureCanViewEmployeeAttendance(Long employeeId) {
        if (CurrentUser.getId().map(id -> id.equals(employeeId)).orElse(false)) return;
        User employee = userService.getUserEntity(employeeId);
        if (CurrentUser.isSuperAdmin()) return;
        Long myId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(myId).orElseThrow();
        if (me.getDepartment() != null && employee.getDepartment() != null && me.getDepartment().getId().equals(employee.getDepartment().getId()))
            return;
        throw new ForbiddenException("Cannot view this employee's attendance");
    }

    private void ensureCanViewTeam(Long teamId) {
        if (CurrentUser.isSuperAdmin()) return;
        Long myId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(myId).orElseThrow();
        if (me.getTeam() != null && me.getTeam().getId().equals(teamId)) return;
        if (me.getDepartment() != null && teamService.getTeamEntity(teamId).getDepartment().getId().equals(me.getDepartment().getId()))
            return;
        throw new ForbiddenException("Cannot view this team");
    }

    private void ensureDepartmentAccess(Long departmentId) {
        if (CurrentUser.isSuperAdmin()) return;
        Long myId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(myId).orElseThrow();
        if (me.getDepartment() == null || !me.getDepartment().getId().equals(departmentId))
            throw new ForbiddenException("Cannot view this department");
    }
}