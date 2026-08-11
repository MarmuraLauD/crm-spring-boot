package com.gym.crmspringboot.service.helper;

import com.gym.crmspringboot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CredentialsService credentialsService;

    @Test
    void generatePassword_ReturnsTenCharacterString() {
        // Arrange
        // Act
        String password = credentialsService.generatePassword();

        // Assert
        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void generateUsername_WithoutConflicts_ReturnsBaseUsername() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        when(userRepository.existsByUsername("John.Doe")).thenReturn(false);

        // Act
        String username = credentialsService.generateUsername(firstName, lastName);

        // Assert
        assertEquals("John.Doe", username);
    }

    @Test
    void generateUsername_WithConflicts_ReturnsUsernameWithSuffix() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        when(userRepository.existsByUsername("John.Doe")).thenReturn(true);
        when(userRepository.existsByUsername("John.Doe1")).thenReturn(true);
        when(userRepository.existsByUsername("John.Doe2")).thenReturn(false);

        // Act
        String username = credentialsService.generateUsername(firstName, lastName);

        // Assert
        assertEquals("John.Doe2", username);
    }
}