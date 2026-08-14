package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.User;
import com.gym.crmspringboot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void changePassword_Success() {
        // Arrange
        String username = "user";
        User user = new User();
        user.setPassword("oldEncoded");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncoded");

        // Act
        userService.changePassword(username, "oldPassword", "newPassword");

        // Assert
        assertEquals("newEncoded", user.getPassword());
    }

    @Test
    void changePassword_ThrowsException_WhenUserNotFound() {
        // Arrange
        String username = "user";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(username, "old", "new"));
    }

    @Test
    void toggleActive_Success() {
        // Arrange
        String username = "user";
        User user = new User();
        user.setActive(false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        userService.toggleActive(username, true);

        // Assert
        assertTrue(user.isActive());
    }
}