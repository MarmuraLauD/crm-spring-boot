package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.Role;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.repository.TrainerRepository;
import com.gym.crmspringboot.service.helper.CredentialsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private CredentialsService credentialsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void create_Success() {
        // Arrange
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Doe");

        when(credentialsService.generatePassword()).thenReturn("rawTrainerPass");
        when(passwordEncoder.encode("rawTrainerPass")).thenReturn("encodedTrainerPass");
        when(credentialsService.generateUsername("Jane", "Doe")).thenReturn("Jane.Doe");
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Trainer result = trainerService.create(trainer);

        // Assert
        assertEquals("Jane.Doe", result.getUsername());
        assertEquals("rawTrainerPass", result.getRawPassword());
        assertEquals("encodedTrainerPass", result.getPassword());
        assertTrue(result.isActive());
        assertEquals(Role.ROLE_TRAINER, result.getRole());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void update_Success() {
        // Arrange
        Trainer trainer = new Trainer();
        trainer.setUsername("Jane.Doe");
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        // Act
        Trainer result = trainerService.update(trainer);

        // Assert
        assertNotNull(result);
        assertEquals("Jane.Doe", result.getUsername());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void findByUsername_Success() {
        // Arrange
        String username = "Jane.Doe";
        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        // Act
        Trainer result = trainerService.findByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void findByUsername_ThrowsException_WhenNotFound() {
        // Arrange
        String username = "Unknown.Trainer";
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(IllegalArgumentException.class, () -> trainerService.findByUsername(username));
    }

    @Test
    void getUnassignedActiveTrainers_Success() {
        // Arrange
        String traineeUsername = "Trainee.One";
        Trainer trainer1 = new Trainer();
        Trainer trainer2 = new Trainer();
        List<Trainer> expectedTrainers = Arrays.asList(trainer1, trainer2);

        when(trainerRepository.getUnassignedActiveTrainers(traineeUsername)).thenReturn(expectedTrainers);

        // Act
        List<Trainer> result = trainerService.getUnassignedActiveTrainers(traineeUsername);

        // Assert
        assertEquals(2, result.size());
        verify(trainerRepository).getUnassignedActiveTrainers(traineeUsername);
    }

}