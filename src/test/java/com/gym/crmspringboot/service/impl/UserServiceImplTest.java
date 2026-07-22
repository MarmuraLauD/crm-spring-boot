package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.User;
import com.gym.crmspringboot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void changePassword_WhenUserExists_ShouldUpdatePassword() {
        // Arrange
        String username = "John.Doe";
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword456";
        User user = new User();
        user.setUsername(username);
        user.setPassword(oldPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        userService.changePassword(username, oldPassword, newPassword);

        // Assert
        assertEquals(newPassword, user.getPassword());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void changePassword_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        String username = "Unknown.User";
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword456";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.changePassword(username, oldPassword, newPassword)
        );
        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void toggleActive_WhenUserExists_ShouldUpdateStatus() {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        Boolean newStatus = false;
        User user = new User();
        user.setUsername(username);
        user.setActive(true);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        userService.toggleActive(username, password, newStatus);

        // Assert
        assertEquals(newStatus, user.isActive());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void toggleActive_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        String username = "Unknown.User";
        String password = "password123";
        Boolean newStatus = false;
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.toggleActive(username, password, newStatus)
        );
        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepository).findByUsername(username);
    }
}