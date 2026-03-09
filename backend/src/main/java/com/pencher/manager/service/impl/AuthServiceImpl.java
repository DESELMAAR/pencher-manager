package com.pencher.manager.service.impl;

import com.pencher.manager.dto.*;
import com.pencher.manager.entity.User;
import com.pencher.manager.entity.enums.RoleType;
import com.pencher.manager.entity.enums.UserStatus;
import com.pencher.manager.exception.BadRequestException;
import com.pencher.manager.mapper.UserMapper;
import com.pencher.manager.repository.UserRepository;
import com.pencher.manager.security.JwtService;
import com.pencher.manager.security.CurrentUser;
import com.pencher.manager.service.AuthService;
import com.pencher.manager.service.DepartmentService;
import com.pencher.manager.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final DepartmentService departmentService;
    private final TeamService teamService;

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new BadRequestException("Invalid email or password");
        }
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refresh = jwtService.generateRefreshToken(user.getId());
        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .expiresIn(jwtService.getAccessExpirationMs() / 1000)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already registered");
        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()
                && userRepository.existsByEmployeeId(request.getEmployeeId()))
            throw new BadRequestException("Employee ID already exists");
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
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
    public TokenResponse refresh(RefreshTokenRequest request) {
        JwtService.JwtClaims claims = jwtService.parseToken(request.getRefreshToken());
        if (claims == null || !"refresh".equals(claims.type()))
            throw new BadRequestException("Invalid refresh token");
        User user = userRepository.findById(claims.userId()).orElseThrow(() -> new BadRequestException("User not found"));
        if (user.getStatus() != UserStatus.ACTIVE)
            throw new BadRequestException("Account is not active");
        String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refresh = jwtService.generateRefreshToken(user.getId());
        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .expiresIn(jwtService.getAccessExpirationMs() / 1000)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public void logout() {
        // Stateless JWT: client discards token. Optional: blacklist refresh token in DB.
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me() {
        Long userId = CurrentUser.getIdOrThrow();
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        return userMapper.toResponse(user);
    }
}
