package com.pencher.manager.service.impl;

import com.pencher.manager.dto.PunchEventResponse;
import com.pencher.manager.entity.PunchEvent;
import com.pencher.manager.entity.User;
import com.pencher.manager.entity.enums.PunchType;
import com.pencher.manager.exception.BadRequestException;
import com.pencher.manager.exception.ForbiddenException;
import com.pencher.manager.mapper.PunchEventMapper;
import com.pencher.manager.repository.PunchEventRepository;
import com.pencher.manager.repository.UserRepository;
import com.pencher.manager.security.CurrentUser;
import com.pencher.manager.service.AttendanceService;
import com.pencher.manager.service.PunchService;
import com.pencher.manager.service.TeamService;
import com.pencher.manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Punch validation: START_WORK must exist before any break or END_SHIFT.
 * Order: START_WORK -> (BREAK_1, BREAK_2, LUNCH_BREAK)* -> END_SHIFT.
 * Duplicate same type on same day is rejected.
 */
@Service
@RequiredArgsConstructor
public class PunchServiceImpl implements PunchService {

    private final PunchEventRepository punchEventRepository;
    private final UserRepository userRepository;
    private final PunchEventMapper punchEventMapper;
    private final AttendanceService attendanceService;
    private final UserService userService;
    private final TeamService teamService;

    @Override
    @Transactional
    public PunchEventResponse punch(PunchType type) {
        Long userId = CurrentUser.getIdOrThrow();
        User user = userService.getUserEntity(userId);
        if (user.getTeam() == null && type != PunchType.START_WORK)
            throw new BadRequestException("Employee must be assigned to a team to punch");
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        Instant now = Instant.now();
        Instant dayStart = today.atStartOfDay(zone).toInstant();
        Instant dayEnd = today.plusDays(1).atStartOfDay(zone).toInstant();

        // Duplicate check: same type on same day
        Optional<PunchEvent> existing = punchEventRepository.findFirstByUserIdAndPunchTypeAndDate(userId, type, dayStart, dayEnd);
        if (existing.isPresent())
            throw new BadRequestException("Already punched " + type + " today");

        if (type == PunchType.START_WORK) {
            PunchEvent event = PunchEvent.builder().user(user).punchType(type).punchAt(now).build();
            event = punchEventRepository.save(event);
            attendanceService.computeAndSaveAttendance(userId, today);
            return punchEventMapper.toResponse(event);
        }

        // For non-START_WORK we require START_WORK to exist today
        Optional<PunchEvent> startWork = punchEventRepository.findFirstByUserIdAndPunchTypeAndDate(userId, PunchType.START_WORK, dayStart, dayEnd);
        if (startWork.isEmpty())
            throw new BadRequestException("Must punch START_WORK before " + type);

        if (type == PunchType.END_SHIFT) {
            PunchEvent event = PunchEvent.builder().user(user).punchType(type).punchAt(now).build();
            event = punchEventRepository.save(event);
            return punchEventMapper.toResponse(event);
        }

        PunchEvent event = PunchEvent.builder().user(user).punchType(type).punchAt(now).build();
        event = punchEventRepository.save(event);
        return punchEventMapper.toResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PunchEventResponse> getMyPunchesToday() {
        Long userId = CurrentUser.getIdOrThrow();
        LocalDate today = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant dayStart = today.atStartOfDay(zone).toInstant();
        Instant dayEnd = today.plusDays(1).atStartOfDay(zone).toInstant();
        return punchEventRepository.findByUserIdAndDate(userId, dayStart, dayEnd).stream()
                .map(punchEventMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PunchEventResponse> getMyPunchesHistory(int page, int size) {
        Long userId = CurrentUser.getIdOrThrow();
        return punchEventRepository.findByUserIdOrderByPunchAtDesc(userId, PageRequest.of(page, size)).stream()
                .map(punchEventMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PunchEventResponse> getPunchesByEmployee(Long employeeId, LocalDate from, LocalDate to) {
        ensureCanViewEmployeePunches(employeeId);
        Instant start = from.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = to.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<PunchEvent> events = punchEventRepository.findByUserIdsAndDateRange(List.of(employeeId), start, end);
        return events.stream().map(punchEventMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PunchEventResponse> getPunchesByTeam(Long teamId, LocalDate date) {
        teamService.getTeamEntity(teamId);
        ensureCanViewTeam(teamId);
        ZoneId zone = ZoneId.systemDefault();
        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();
        return punchEventRepository.findByTeamIdAndDate(teamId, dayStart, dayEnd).stream()
                .map(punchEventMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PunchEventResponse> getPunchesByDepartment(Long departmentId, LocalDate date) {
        ensureDepartmentAccess(departmentId);
        ZoneId zone = ZoneId.systemDefault();
        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();
        return punchEventRepository.findByDepartmentIdAndDate(departmentId, dayStart, dayEnd).stream()
                .map(punchEventMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void ensureCanViewEmployeePunches(Long employeeId) {
        if (CurrentUser.getId().map(id -> id.equals(employeeId)).orElse(false)) return;
        User employee = userService.getUserEntity(employeeId);
        if (CurrentUser.isSuperAdmin()) return;
        Long myId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(myId).orElseThrow();
        if (me.getDepartment() != null && employee.getDepartment() != null && me.getDepartment().getId().equals(employee.getDepartment().getId()))
            return;
        throw new ForbiddenException("Cannot view this employee's punches");
    }

    private void ensureCanViewTeam(Long teamId) {
        if (CurrentUser.isSuperAdmin()) return;
        Long myId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(myId).orElseThrow();
        if (me.getTeam() != null && me.getTeam().getId().equals(teamId)) return;
        if (me.getDepartment() != null && teamService.getTeamEntity(teamId).getDepartment().getId().equals(me.getDepartment().getId()))
            return;
        throw new ForbiddenException("Cannot view this team's punches");
    }

    private void ensureDepartmentAccess(Long departmentId) {
        if (CurrentUser.isSuperAdmin()) return;
        Long myId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(myId).orElseThrow();
        if (me.getDepartment() == null || !me.getDepartment().getId().equals(departmentId))
            throw new ForbiddenException("Cannot view this department's data");
    }
}
