package com.healthware.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    @BeforeEach
    void setUp() {
        token = jwtUtil.generateToken(1L, "testuser", "user");
    }

    @Test
    void generateToken_Success() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void parseToken_Success() {
        Claims claims = jwtUtil.parseToken(token);

        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertEquals(1L, claims.get("userId", Long.class));
        assertEquals("user", claims.get("role", String.class));
    }

    @Test
    void isTokenExpired_NotExpired() {
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void getUserId_Success() {
        Long userId = jwtUtil.getUserId(token);

        assertEquals(1L, userId);
    }

    @Test
    void getUsername_Success() {
        String username = jwtUtil.getUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void getRole_Success() {
        String role = jwtUtil.getRole(token);

        assertEquals("user", role);
    }

    @Test
    void generateToken_AdminRole() {
        String adminToken = jwtUtil.generateToken(1L, "admin", "admin");
        Claims claims = jwtUtil.parseToken(adminToken);

        assertEquals("admin", claims.get("role", String.class));
    }

    @Test
    void parseToken_InvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.parseToken("invalid.token.here"));
    }
}
