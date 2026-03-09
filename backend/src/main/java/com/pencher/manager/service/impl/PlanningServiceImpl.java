package com.pencher.manager.service.impl;

import com.pencher.manager.dto.PlanningRecordResponse;
import com.pencher.manager.entity.PlanningRecord;
import com.pencher.manager.entity.User;
import com.pencher.manager.exception.ForbiddenException;
import com.pencher.manager.integration.PlanningApiClient;
import com.pencher.manager.integration.dto.PlanningApiResponse;
import com.pencher.manager.mapper.PlanningRecordMapper;
import com.pencher.manager.repository.PlanningRecordRepository;
import com.pencher.manager.repository.UserRepository;
import com.pencher.manager.security.CurrentUser;
import com.pencher.manager.service.PlanningService;
import com.pencher.manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanningServiceImpl implements PlanningService {

    private final PlanningRecordRepository planningRecordRepository;
    private final PlanningApiClient planningApiClient;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PlanningRecordMapper planningRecordMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<PlanningRecordResponse> getPlanningForUserAndDate(Long userId, LocalDate date) {
        return planningRecordRepository.findByUserIdAndPlanDate(userId, date)
                .map(pr -> planningRecordMapper.toResponse(pr, userService.getUserEntity(userId).getEmployeeId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanningRecordResponse> getPlanningForEmployee(Long employeeId, LocalDate from, LocalDate to) {
        ensureCanViewEmployee(employeeId);
        User user = userService.getUserEntity(employeeId);
        List<PlanningRecord> list = planningRecordRepository.findByUserIdAndPlanDateBetweenOrderByPlanDateDesc(employeeId, from, to);
        return list.stream()
                .map(pr -> planningRecordMapper.toResponse(pr, user.getEmployeeId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void syncPlanningForEmployee(Long employeeId) {
        ensureCanSyncPlanning();
        User user = userService.getUserEntity(employeeId);
        String empId = user.getEmployeeId() != null ? user.getEmployeeId() : String.valueOf(employeeId);
        List<PlanningApiResponse> apiList = planningApiClient.getPlanningForEmployee(empId, LocalDate.now(), LocalDate.now().plusDays(30));
        for (PlanningApiResponse r : apiList) {
            PlanningRecord rec = planningRecordRepository.findByUserIdAndPlanDate(employeeId, r.getDate())
                    .orElse(new PlanningRecord());
            rec.setUser(user);
            rec.setPlanDate(r.getDate());
            rec.setPlannedStartTime(r.getPlannedStartTime());
            rec.setPlannedEndTime(r.getPlannedEndTime());
            rec.setScheduled(r.getScheduled());
            rec.setShiftType(r.getShiftType());
            planningRecordRepository.save(rec);
        }
    }

    @Override
    @Transactional
    public void syncPlanningForAll() {
        if (!CurrentUser.isSuperAdmin()) throw new ForbiddenException("Only Super Admin can sync all planning");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getEmployeeId() != null || user.getRole().name().equals("EMPLOYEE")) {
                try {
                    syncPlanningForEmployee(user.getId());
                } catch (Exception ignored) {}
            }
        }
    }

    private void ensureCanViewEmployee(Long employeeId) {
        if (CurrentUser.getId().map(id -> id.equals(employeeId)).orElse(false)) return;
        if (CurrentUser.isSuperAdmin()) return;
        User me = userRepository.findById(CurrentUser.getIdOrThrow()).orElseThrow();
        User employee = userService.getUserEntity(employeeId);
        if (me.getDepartment() != null && employee.getDepartment() != null && me.getDepartment().getId().equals(employee.getDepartment().getId()))
            return;
        throw new ForbiddenException("Cannot view this employee's planning");
    }

    private void ensureCanSyncPlanning() {
        if (CurrentUser.isSuperAdmin()) return;
        if (CurrentUser.getRole().orElse(null) == com.pencher.manager.entity.enums.RoleType.DEPARTMENT_ADMIN) return;
        throw new ForbiddenException("Cannot sync planning");
    }
}
