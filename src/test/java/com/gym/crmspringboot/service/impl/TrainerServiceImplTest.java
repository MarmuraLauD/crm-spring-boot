package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.repository.TrainerRepository;
import com.gym.crmspringboot.service.helper.CredentialsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private CredentialsService credentialsService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void create_ShouldGenerateCredentialsAndSave() {
        // Arrange
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        String expectedPassword = "generatedPassword456";
        String expectedUsername = "Jane.Smith";

        when(credentialsService.generatePassword()).thenReturn(expectedPassword);
        when(credentialsService.generateUsername("Jane", "Smith")).thenReturn(expectedUsername);
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        // Act
        Trainer savedTrainer = trainerService.create(trainer);

        // Assert
        assertEquals(expectedPassword, savedTrainer.getPassword());
        assertEquals(expectedUsername, savedTrainer.getUsername());
        assertTrue(savedTrainer.isActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void update_ShouldSaveTrainer() {
        // Arrange
        String username = "Jane.Smith";
        String password = "password123";
        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        // Act
        Trainer updatedTrainer = trainerService.update(username, password, trainer);

        // Assert
        assertEquals(trainer, updatedTrainer);
        verify(trainerRepository).save(trainer);
    }

    @Test
    void findByUsername_ShouldReturnOptionalTrainer() {
        // Arrange
        String username = "Jane.Smith";
        String password = "password123";
        Trainer trainer = new Trainer();
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        // Act
        Trainer result = trainerService.findByUsername(username, password);

        // Assert
        assertEquals(trainer, result);
        verify(trainerRepository).findByUsername(username);
    }

    @Test
    void getUnassignedActiveTrainers_ShouldReturnListOfTrainers() {
        // Arrange
        String username = "Admin.User";
        String password = "password123";
        String traineeUsername = "John.Doe";

        Trainer trainer1 = new Trainer();
        trainer1.setUsername("Trainer.One");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("Trainer.Two");
        List<Trainer> expectedTrainers = Arrays.asList(trainer1, trainer2);

        when(trainerRepository.getUnassignedActiveTrainers(traineeUsername)).thenReturn(expectedTrainers);

        // Act
        List<Trainer> actualTrainers = trainerService.getUnassignedActiveTrainers(username, password, traineeUsername);

        // Assert
        assertEquals(expectedTrainers.size(), actualTrainers.size());
        assertEquals(expectedTrainers, actualTrainers);
        verify(trainerRepository).getUnassignedActiveTrainers(traineeUsername);
    }
}