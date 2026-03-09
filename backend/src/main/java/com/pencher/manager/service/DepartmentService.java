package com.pencher.manager.service;

import com.pencher.manager.dto.DepartmentRequest;
import com.pencher.manager.dto.DepartmentResponse;
import com.pencher.manager.entity.Department;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse create(DepartmentRequest request);
    List<DepartmentResponse> findAll();
    DepartmentResponse findById(Long id);
    DepartmentResponse update(Long id, DepartmentRequest request);
    void delete(Long id);
    Department getDepartmentEntity(Long id);
}
