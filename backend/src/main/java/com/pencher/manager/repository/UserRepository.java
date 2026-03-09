package com.pencher.manager.repository;

import com.pencher.manager.entity.User;
import com.pencher.manager.entity.enums.RoleType;
import com.pencher.manager.entity.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    List<User> findByDepartmentId(Long departmentId);
    List<User> findByTeamId(Long teamId);
    List<User> findByDepartmentIdAndRole(Long departmentId, RoleType role);
    List<User> findByTeamIdAndRole(Long teamId, RoleType role);
    long countByStatus(UserStatus status);
}
