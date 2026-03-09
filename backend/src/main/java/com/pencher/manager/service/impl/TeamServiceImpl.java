package com.pencher.manager.service.impl;

import com.pencher.manager.dto.TeamRequest;
import com.pencher.manager.dto.TeamResponse;
import com.pencher.manager.entity.Department;
import com.pencher.manager.entity.Team;
import com.pencher.manager.entity.User;
import com.pencher.manager.entity.enums.RoleType;
import com.pencher.manager.exception.BadRequestException;
import com.pencher.manager.exception.ForbiddenException;
import com.pencher.manager.exception.ResourceNotFoundException;
import com.pencher.manager.mapper.TeamMapper;
import com.pencher.manager.repository.TeamRepository;
import com.pencher.manager.repository.UserRepository;
import com.pencher.manager.security.CurrentUser;
import com.pencher.manager.service.DepartmentService;
import com.pencher.manager.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMapper teamMapper;
    private final DepartmentService departmentService;

    @Override
    @Transactional
    public TeamResponse create(TeamRequest request) {
        if (!CurrentUser.isSuperAdmin() && !CurrentUser.hasRole(RoleType.DEPARTMENT_ADMIN))
            throw new ForbiddenException("Only Super Admin or Department Admin can create teams");
        Department dept = departmentService.getDepartmentEntity(request.getDepartmentId());
        checkDepartmentAccess(dept.getId());
        Team t = new Team();
        t.setName(request.getName());
        t.setDescription(request.getDescription());
        t.setDepartment(dept);
        t = teamRepository.save(t);
        return teamMapper.toResponse(t, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> findAll() {
        List<Team> list = teamRepository.findAll();
        return filterByRoleAndMap(list);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse findById(Long id) {
        Team t = getTeamEntity(id);
        checkTeamAccess(t);
        return teamMapper.toResponse(t, leaderName(t.getLeaderUserId()));
    }

    @Override
    @Transactional
    public TeamResponse update(Long id, TeamRequest request) {
        if (!CurrentUser.isSuperAdmin() && !CurrentUser.hasRole(RoleType.DEPARTMENT_ADMIN))
            throw new ForbiddenException("Only Super Admin or Department Admin can update teams");
        Team t = getTeamEntity(id);
        checkDepartmentAccess(t.getDepartment().getId());
        t.setName(request.getName());
        t.setDescription(request.getDescription());
        if (request.getDepartmentId() != null && !request.getDepartmentId().equals(t.getDepartment().getId())) {
            t.setDepartment(departmentService.getDepartmentEntity(request.getDepartmentId()));
        }
        t = teamRepository.save(t);
        return teamMapper.toResponse(t, leaderName(t.getLeaderUserId()));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!CurrentUser.isSuperAdmin() && !CurrentUser.hasRole(RoleType.DEPARTMENT_ADMIN))
            throw new ForbiddenException("Only Super Admin or Department Admin can delete teams");
        Team t = getTeamEntity(id);
        checkDepartmentAccess(t.getDepartment().getId());
        teamRepository.delete(t);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> findByDepartmentId(Long departmentId) {
        departmentService.getDepartmentEntity(departmentId);
        checkDepartmentAccess(departmentId);
        List<Team> list = teamRepository.findByDepartmentId(departmentId);
        return list.stream().map(team -> teamMapper.toResponse(team, leaderName(team.getLeaderUserId()))).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Team getTeamEntity(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
    }

    private List<TeamResponse> filterByRoleAndMap(List<Team> list) {
        if (CurrentUser.isSuperAdmin())
            return list.stream().map(t -> teamMapper.toResponse(t, leaderName(t.getLeaderUserId()))).collect(Collectors.toList());
        Long currentId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(currentId).orElseThrow();
        if (me.getDepartment() == null) return List.of();
        Long deptId = me.getDepartment().getId();
        return list.stream()
                .filter(t -> t.getDepartment() != null && t.getDepartment().getId().equals(deptId))
                .map(t -> teamMapper.toResponse(t, leaderName(t.getLeaderUserId())))
                .collect(Collectors.toList());
    }

    private void checkDepartmentAccess(Long departmentId) {
        if (CurrentUser.isSuperAdmin()) return;
        Long currentId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(currentId).orElseThrow();
        if (me.getDepartment() == null || !me.getDepartment().getId().equals(departmentId))
            throw new ForbiddenException("Access denied to this department");
    }

    private void checkTeamAccess(Team team) {
        if (CurrentUser.isSuperAdmin()) return;
        Long currentId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(currentId).orElseThrow();
        if (me.getDepartment() != null && team.getDepartment() != null && me.getDepartment().getId().equals(team.getDepartment().getId()))
            return;
        if (me.getTeam() != null && me.getTeam().getId().equals(team.getId())) return;
        throw new ForbiddenException("Access denied to this team");
    }

    private String leaderName(Long leaderUserId) {
        if (leaderUserId == null) return null;
        return userRepository.findById(leaderUserId).map(User::getFullName).orElse(null);
    }
}
