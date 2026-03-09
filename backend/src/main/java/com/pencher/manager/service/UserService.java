package com.pencher.manager.service;

import com.pencher.manager.dto.UserRequest;
import com.pencher.manager.dto.UserResponse;
import com.pencher.manager.entity.User;
import com.pencher.manager.entity.enums.UserStatus;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    UserResponse update(Long id, UserRequest request);
    UserResponse patchStatus(Long id, UserStatus status);
    void delete(Long id);
    List<UserResponse> findEmployeesByTeamId(Long teamId);
    User getUserEntity(Long id);
}
