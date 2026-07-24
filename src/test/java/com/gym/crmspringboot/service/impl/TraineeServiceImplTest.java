package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.repository.TraineeRepository;
import com.gym.crmspringboot.repository.TrainerRepository;
import com.gym.crmspringboot.service.helper.CredentialsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private CredentialsService credentialsService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void createTrainee_ShouldGenerateCredentialsAndSave() {
        // Arrange
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        String expectedPassword = "generatedPassword123";
        String expectedUsername = "John.Doe";

        when(credentialsService.generatePassword()).thenReturn(expectedPassword);
        when(credentialsService.generateUsername("John", "Doe")).thenReturn(expectedUsername);
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        // Act
        Trainee savedTrainee = traineeService.createTrainee(trainee);

        // Assert
        assertEquals(expectedPassword, savedTrainee.getPassword());
        assertEquals(expectedUsername, savedTrainee.getUsername());
        assertTrue(savedTrainee.isActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTrainee_ShouldSaveTrainee() {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        // Act
        Trainee updatedTrainee = traineeService.updateTrainee(username, password, trainee);

        // Assert
        assertEquals(trainee, updatedTrainee);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void deleteTrainee_WhenExists_ShouldDeleteByUsername() {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        // Act
        traineeService.deleteTrainee(username, password);

        // Assert
        verify(traineeRepository).deleteByUsername(username);
    }

    @Test
    void deleteTrainee_WhenNotFound_ShouldThrowException() {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.deleteTrainee(username, password)
        );
        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeRepository, never()).deleteByUsername(any());
    }

    @Test
    void findByUsername_ShouldReturnOptionalTrainee() {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        // Act
        Trainee result = traineeService.findByUsername(username, password);

        // Assert
        assertEquals(trainee, result);
        verify(traineeRepository).findByUsername(username);
    }

    @Test
    void updateTrainersList_WhenTraineeExists_ShouldUpdateAndReturnTrainers() {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        List<String> trainerUsernames = Arrays.asList("Trainer.One", "Trainer.Two");

        Trainee trainee = new Trainee();
        trainee.setTrainers(new HashSet<>());

        Trainer trainer1 = new Trainer();
        trainer1.setUsername("Trainer.One");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("Trainer.Two");
        List<Trainer> foundTrainers = Arrays.asList(trainer1, trainer2);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsernameIn(trainerUsernames)).thenReturn(foundTrainers);

        // Act
        List<Trainer> updatedTrainers = traineeService.updateTrainersList(username, password, trainerUsernames);

        // Assert
        assertEquals(2, updatedTrainers.size());
        assertTrue(updatedTrainers.contains(trainer1));
        assertTrue(updatedTrainers.contains(trainer2));
        verify(traineeRepository).findByUsername(username);
        verify(trainerRepository).findByUsernameIn(trainerUsernames);
    }

    @Test
    void updateTrainersList_WhenTraineeNotFound_ShouldThrowException() {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        List<String> trainerUsernames = List.of("Trainer.One");
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.updateTrainersList(username, password, trainerUsernames)
        );
        assertEquals("Trainee not found", exception.getMessage());
        verify(trainerRepository, never()).findByUsernameIn(any());
    }
}