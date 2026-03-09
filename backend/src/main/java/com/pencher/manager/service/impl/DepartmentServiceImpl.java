package com.pencher.manager.service.impl;

import com.pencher.manager.dto.DepartmentRequest;
import com.pencher.manager.dto.DepartmentResponse;
import com.pencher.manager.entity.Department;
import com.pencher.manager.entity.User;
import com.pencher.manager.entity.enums.RoleType;
import com.pencher.manager.exception.BadRequestException;
import com.pencher.manager.exception.ForbiddenException;
import com.pencher.manager.exception.ResourceNotFoundException;
import com.pencher.manager.mapper.DepartmentMapper;
import com.pencher.manager.repository.DepartmentRepository;
import com.pencher.manager.repository.UserRepository;
import com.pencher.manager.security.CurrentUser;
import com.pencher.manager.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (!CurrentUser.isSuperAdmin()) throw new ForbiddenException("Only Super Admin can create departments");
        if (departmentRepository.findAll().stream().anyMatch(d -> d.getName().equalsIgnoreCase(request.getName())))
            throw new BadRequestException("Department name already exists");
        Department d = new Department();
        d.setName(request.getName());
        d.setDescription(request.getDescription());
        d = departmentRepository.save(d);
        return departmentMapper.toResponse(d, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll() {
        List<Department> list = departmentRepository.findAll();
        if (CurrentUser.isSuperAdmin())
            return list.stream().map(d -> departmentMapper.toResponse(d, adminName(d.getAdminUserId()))).collect(Collectors.toList());
        Long currentId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(currentId).orElseThrow();
        if (me.getRole() == RoleType.DEPARTMENT_ADMIN && me.getDepartment() != null) {
            return list.stream()
                    .filter(d -> d.getId().equals(me.getDepartment().getId()))
                    .map(d -> departmentMapper.toResponse(d, adminName(d.getAdminUserId())))
                    .collect(Collectors.toList());
        }
        if (me.getRole() == RoleType.TEAM_LEADER && me.getDepartment() != null) {
            return list.stream()
                    .filter(d -> d.getId().equals(me.getDepartment().getId()))
                    .map(d -> departmentMapper.toResponse(d, adminName(d.getAdminUserId())))
                    .collect(Collectors.toList());
        }
        return list.stream().map(d -> departmentMapper.toResponse(d, adminName(d.getAdminUserId()))).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse findById(Long id) {
        Department d = getDepartmentEntity(id);
        checkDepartmentAccess(d.getId());
        return departmentMapper.toResponse(d, adminName(d.getAdminUserId()));
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        if (!CurrentUser.isSuperAdmin()) throw new ForbiddenException("Only Super Admin can update departments");
        Department d = getDepartmentEntity(id);
        d.setName(request.getName());
        d.setDescription(request.getDescription());
        d = departmentRepository.save(d);
        return departmentMapper.toResponse(d, adminName(d.getAdminUserId()));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!CurrentUser.isSuperAdmin()) throw new ForbiddenException("Only Super Admin can delete departments");
        Department d = getDepartmentEntity(id);
        departmentRepository.delete(d);
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentEntity(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }

    private void checkDepartmentAccess(Long departmentId) {
        if (CurrentUser.isSuperAdmin()) return;
        Long currentId = CurrentUser.getIdOrThrow();
        User me = userRepository.findById(currentId).orElseThrow();
        if (me.getDepartment() != null && me.getDepartment().getId().equals(departmentId)) return;
        throw new ForbiddenException("Access denied to this department");
    }

    private String adminName(Long adminUserId) {
        if (adminUserId == null) return null;
        return userRepository.findById(adminUserId).map(User::getFullName).orElse(null);
    }
}
