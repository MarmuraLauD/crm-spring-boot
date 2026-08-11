package com.gym.crmspringboot.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginAttemptServiceImplTest {

    private LoginAttemptServiceImpl loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptServiceImpl();
    }

    @Test
    void isBlocked_ReturnsFalse_Initially() {
        // Arrange
        String username = "testUser";

        // Act
        boolean blocked = loginAttemptService.isBlocked(username);

        // Assert
        assertFalse(blocked);
    }

    @Test
    void loginFailed_BlocksUser_AfterThreeAttempts() {
        // Arrange
        String username = "testUser";

        // Act
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        boolean blocked = loginAttemptService.isBlocked(username);

        // Assert
        assertTrue(blocked);
    }

    @Test
    void loginSucceeded_ResetsAttempts() {
        // Arrange
        String username = "testUser";
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);

        // Act
        loginAttemptService.loginSucceeded(username);
        loginAttemptService.loginFailed(username);
        boolean blocked = loginAttemptService.isBlocked(username);

        // Assert
        assertFalse(blocked);
    }

}
