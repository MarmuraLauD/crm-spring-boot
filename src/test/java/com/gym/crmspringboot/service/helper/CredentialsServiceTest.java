package com.gym.crmspringboot.service.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gym.crmspringboot.model.User;
import com.gym.crmspringboot.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CredentialsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CredentialsService credentialsService;

    @Test
    void generatePassword_ShouldReturnStringOfCorrectLength() {
        // Arrange
        int expectedLength = 10;

        // Act
        String password = credentialsService.generatePassword();

        // Assert
        assertNotNull(password);
        assertEquals(expectedLength, password.length());
    }

    @Test
    void generateUsername_WhenUserDoesNotExist_ShouldReturnBaseUsername() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String expectedUsername = "John.Doe";
        when(userRepository.existsByUsername(expectedUsername)).thenReturn(false);

        // Act
        String actualUsername = credentialsService.generateUsername(firstName, lastName);

        // Assert
        assertEquals(expectedUsername, actualUsername);
        verify(userRepository).existsByUsername(expectedUsername);
    }

    @Test
    void generateUsername_WhenUserExists_ShouldReturnSuffixedUsername() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        when(userRepository.existsByUsername("John.Doe")).thenReturn(true);
        when(userRepository.existsByUsername("John.Doe1")).thenReturn(true);
        when(userRepository.existsByUsername("John.Doe2")).thenReturn(false);

        // Act
        String actualUsername = credentialsService.generateUsername(firstName, lastName);

        // Assert
        assertEquals("John.Doe2", actualUsername);
        verify(userRepository, times(3)).existsByUsername(anyString());
    }

    @Test
    void authenticate_WhenCredentialsMatch_ShouldReturnTrue() {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        User user = new User();
        user.setPassword(password);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        boolean isAuthenticated = credentialsService.authenticate(username, password);

        // Assert
        assertTrue(isAuthenticated);
    }

    @Test
    void authenticate_WhenPasswordMismatches_ShouldReturnFalse() {
        // Arrange
        String username = "John.Doe";
        String correctPassword = "password123";
        String wrongPassword = "wrongPassword";
        User user = new User();
        user.setPassword(correctPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        boolean isAuthenticated = credentialsService.authenticate(username, wrongPassword);

        // Assert
        assertFalse(isAuthenticated);
    }

    @Test
    void authenticate_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        boolean isAuthenticated = credentialsService.authenticate(username, password);

        // Assert
        assertFalse(isAuthenticated);
    }
}