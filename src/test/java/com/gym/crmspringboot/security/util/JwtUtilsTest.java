package com.gym.crmspringboot.security.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "NEQ0RTYzNTI2NjU1NkE1ODZFMzI3MjM1NzUzODc4MkY0MTNGNDQyODQ3MkI0QjYyNTA2NDUzNjc1NjZCNTk3MA==");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 900000L); // 15 минут
        ReflectionTestUtils.setField(jwtUtils, "refreshExpirationMs", 604800000L); // 7 дней

        userDetails = new User("testUser", "password", Collections.emptyList());
    }

    @Test
    void generateAccessToken_AndValidate_ReturnsTrue() {
        // Arrange
        String token;

        // Act
        token = jwtUtils.generateAccessToken(userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token, userDetails));
    }

    @Test
    void extractUsername_ReturnsCorrectUsername() {
        // Arrange
        String token = jwtUtils.generateAccessToken(userDetails);

        // Act
        String username = jwtUtils.extractUsername(token);

        // Assert
        assertEquals("testUser", username);
    }

    @Test
    void extractExpiration_ReturnsFutureDate() {
        // Arrange
        String token = jwtUtils.generateAccessToken(userDetails);

        // Act
        Date expiration = jwtUtils.extractExpiration(token);

        // Assert
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_WithDifferentUser_ReturnsFalse() {
        // Arrange
        String token = jwtUtils.generateAccessToken(userDetails);
        UserDetails wrongUser = new User("wrongUser", "password", Collections.emptyList());

        // Act
        boolean isValid = jwtUtils.validateToken(token, wrongUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void generateRefreshJwtCookie_ReturnsConfiguredCookie() {
        // Arrange
        String refreshToken = "sample_refresh_token";

        // Act
        ResponseCookie cookie = jwtUtils.generateRefreshJwtCookie(refreshToken);

        // Assert
        assertEquals("refresh_token", cookie.getName());
        assertEquals(refreshToken, cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/api/v1/auth/refresh", cookie.getPath());
        assertEquals(604800L, cookie.getMaxAge().getSeconds());
    }

    @Test
    void getCleanJwtRefreshCookie_ReturnsEmptyCookieWithZeroMaxAge() {
        // Arrange
        // Act
        ResponseCookie cookie = jwtUtils.getCleanJwtRefreshCookie();

        // Assert
        assertEquals("refresh_token", cookie.getName());
        assertEquals("", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/api/v1/auth/refresh", cookie.getPath());
        assertEquals(0L, cookie.getMaxAge().getSeconds());
    }

}
