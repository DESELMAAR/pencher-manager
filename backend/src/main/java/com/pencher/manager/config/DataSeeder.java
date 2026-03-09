package com.pencher.manager.config;

import com.pencher.manager.entity.Department;
import com.pencher.manager.entity.Team;
import com.pencher.manager.entity.User;
import com.pencher.manager.entity.enums.RoleType;
import com.pencher.manager.entity.enums.UserStatus;
import com.pencher.manager.repository.DepartmentRepository;
import com.pencher.manager.repository.TeamRepository;
import com.pencher.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Instant;

/**
 * Seeds initial data when running with profile 'dev' or 'seed'.
 * Creates Super Admin and demo department/team/employees.
 */
@Component
@Profile({"dev", "seed"})
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByEmail("superadmin@pencher.com").isPresent())
            return;
        User superAdmin = User.builder()
                .fullName("Super Admin")
                .email("superadmin@pencher.com")
                .password(passwordEncoder.encode("SuperAdmin123!"))
                .status(UserStatus.ACTIVE)
                .employeeId("SA001")
                .role(RoleType.SUPER_ADMIN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        superAdmin = userRepository.save(superAdmin);

        Department dept = new Department();
        dept.setName("IT");
        dept.setDescription("Information Technology");
        dept.setCreatedAt(Instant.now());
        dept.setUpdatedAt(Instant.now());
        dept = departmentRepository.save(dept);

        Team team = new Team();
        team.setName("Development");
        team.setDepartment(dept);
        team.setCreatedAt(Instant.now());
        team.setUpdatedAt(Instant.now());
        team = teamRepository.save(team);

        User deptAdmin = User.builder()
                .fullName("Department Admin")
                .email("deptadmin@pencher.com")
                .password(passwordEncoder.encode("DeptAdmin123!"))
                .status(UserStatus.ACTIVE)
                .employeeId("DA001")
                .role(RoleType.DEPARTMENT_ADMIN)
                .department(dept)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        deptAdmin = userRepository.save(deptAdmin);
        dept.setAdminUserId(deptAdmin.getId());
        departmentRepository.save(dept);

        User teamLeader = User.builder()
                .fullName("Team Leader")
                .email("teamleader@pencher.com")
                .password(passwordEncoder.encode("TeamLeader123!"))
                .status(UserStatus.ACTIVE)
                .employeeId("TL001")
                .role(RoleType.TEAM_LEADER)
                .department(dept)
                .team(team)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        teamLeader = userRepository.save(teamLeader);
        team.setLeaderUserId(teamLeader.getId());
        teamRepository.save(team);

        User employee = User.builder()
                .fullName("John Employee")
                .email("employee@pencher.com")
                .password(passwordEncoder.encode("Employee123!"))
                .status(UserStatus.ACTIVE)
                .employeeId("EMP001")
                .phoneNumber("+1234567890")
                .hiringDate(LocalDate.now().minusYears(1))
                .role(RoleType.EMPLOYEE)
                .department(dept)
                .team(team)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(employee);
    }
}
