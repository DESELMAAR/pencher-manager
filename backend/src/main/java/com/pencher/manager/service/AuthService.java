package com.pencher.manager.service;

import com.pencher.manager.dto.*;

public interface AuthService {
    TokenResponse login(LoginRequest request);
    UserResponse register(RegisterRequest request);
    TokenResponse refresh(RefreshTokenRequest request);
    void logout();
    UserResponse me();
}
