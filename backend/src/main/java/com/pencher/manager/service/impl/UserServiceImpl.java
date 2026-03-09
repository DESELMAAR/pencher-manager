package com.pencher.manager.service.impl;

import com.pencher.manager.dto.UserRequest;
import com.pencher.manager.dto.UserResponse;
import com.pencher.manager.entity.Department;
import com.pencher.manager.entity.Team;
import com.pencher.manager.entity.User;
import com.pencher.manager.entity.enums.RoleType;
import com.pencher.manager.entity.enums.UserStatus;
import com.pencher.manager.exception.BadRequestException;
import com.pencher.manager.exception.ForbiddenException;
import com.pencher.manager.exception.ResourceNotFoundException;
import com.pencher.manager.mapper.UserMapper;
import com.pencher.manager.repository.UserRepository;
import com.pencher.manager.security.CurrentUser;
import com.pencher.manager.service.DepartmentService;
import com.pencher.manager.service.TeamService;
import com.pencher.manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentService departmentService;
    private final TeamService teamService;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        ensureCanManageUsers();
        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already registered");
        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()
                && userRepository.existsByEmployeeId(request.getEmployeeId()))
            throw new BadRequestException("Employee ID already exists");
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword() != null ? request.getPassword() : "ChangeMe123"));
        user.setStatus(UserStatus.ACTIVE);
        user.setEmployeeId(request.getEmployeeId());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setHiringDate(request.getHiringDate());
        user.setRole(request.getRole());
        if (request.getDepartmentId() != null)
            user.setDepartment(departmentService.getDepartmentEntity(request.getDepartmentId()));
        if (request.getTeamId() != null)
            user.setTeam(teamService.getTeamEntity(request.getTeamId()));
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<User> list = userRepository.findAll();
        return filterByRoleAndMap(list);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = getUserEntity(id);
        checkUserAccess(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        ensureCanManageUsers();
        User user = getUserEntity(id);
        checkUserAccess(user);
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already registered");
        if (request.getEmployeeId() != null && !request.getEmployeeId().equals(user.getEmployeeId())
                && userRepository.existsByEmployeeId(request.getEmployeeId()))
            throw new BadRequestException("Employee ID already exists");
        userMapper.updateEntity(user, request);
        if (request.getPassword() != null && !request.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getDepartmentId() != null)
            user.setDepartment(departmentService.getDepartmentEntity(request.getDepartmentId()));
        else if (request.getDepartmentId() == null && (request.getTeamId() == null))
            user.setDepartment(null);
        if (request.getTeamId() != null)
            user.setTeam(teamService.getTeamEntity(request.getTeamId()));
        else
            user.setTeam(null);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse patchStatus(Long id, UserStatus status) {
        ensureCanManageUsers();
        User user = getUserEntity(id);
        checkUserAccess(user);
        user.setStatus(status);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ensureCanManageUsers();
        User user = getUserEntity(id);
        checkUserAccess(user);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findEmployeesByTeamId(Long teamId) {
        Team team = teamService.getTeamEntity(teamId);
        checkTeamAccess(team);
        return userRepository.findByTeamId(teamId).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private void ensureCanManageUsers() {
        if (CurrentUser.isSuperAdmin()) return;
        if (CurrentUser.hasRole(RoleType.DEPARTMENT_ADMIN)) return;
        if (CurrentUser.hasRole(RoleType.TEAM_LEADER)) return;
        throw new ForbiddenException("Not allowed to manage users");
    }

    private List<UserResponse> filterByRoleAndMap(List<User> list) {
        if (CurrentUser.isSuperAdmin())
            return list.stream().map(userMapper::toResponse).collect(Collectors.toList());
        Long currentId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(currentId).orElseThrow();
        if (me.getDepartment() == null) return List.of();
        Long deptId = me.getDepartment().getId();
        return list.stream()
                .filter(u -> u.getDepartment() != null && u.getDepartment().getId().equals(deptId))
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void checkUserAccess(User user) {
        if (CurrentUser.isSuperAdmin()) return;
        Long currentId = CurrentUser.getIdOrThrow();
        if (user.getId().equals(currentId)) return;
        User me = userRepository.findById(currentId).orElseThrow();
        if (me.getDepartment() != null && user.getDepartment() != null && me.getDepartment().getId().equals(user.getDepartment().getId()))
            return;
        throw new ForbiddenException("Access denied to this user");
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
}
