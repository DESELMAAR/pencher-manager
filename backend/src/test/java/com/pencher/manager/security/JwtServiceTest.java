package com.pencher.manager.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "test-secret-key-at-least-256-bits-long-for-hs256",
                900_000,
                604_800_000
        );
    }

    @Test
    void generatesAndParsesAccessToken() {
        String token = jwtService.generateAccessToken(1L, "user@test.com", "EMPLOYEE");
        assertNotNull(token);
        JwtService.JwtClaims claims = jwtService.parseToken(token);
        assertNotNull(claims);
        assertEquals(1L, claims.userId());
        assertEquals("user@test.com", claims.email());
        assertEquals("EMPLOYEE", claims.role());
        assertEquals("access", claims.type());
    }

    @Test
    void parseTokenReturnsNullForInvalidToken() {
        assertNull(jwtService.parseToken("invalid"));
    }
}
