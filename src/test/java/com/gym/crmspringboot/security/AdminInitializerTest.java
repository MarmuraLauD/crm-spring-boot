package com.gym.crmspringboot.security;

import com.gym.crmspringboot.model.User;
import com.gym.crmspringboot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminInitializer adminInitializer;

    @Test
    void run_CreatesAdmin_WhenNotExists() {
        // Arrange
        ReflectionTestUtils.setField(adminInitializer, "adminUsername", "admin");
        ReflectionTestUtils.setField(adminInitializer, "adminPassword", "password");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act
        adminInitializer.run();

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    void run_SkipsCreation_WhenAdminExists() {
        // Arrange
        ReflectionTestUtils.setField(adminInitializer, "adminUsername", "admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(new User()));

        // Act
        adminInitializer.run();

        // Assert
        verify(userRepository, never()).save(any(User.class));
    }
}