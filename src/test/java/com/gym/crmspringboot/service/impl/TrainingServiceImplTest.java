package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void create_Success() {
        // Arrange
        Training training = new Training();
        training.setName("Yoga Basics");
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        // Act
        Training result = trainingService.create(training);

        // Assert
        assertEquals("Yoga Basics", result.getName());
        verify(trainingRepository).save(training);
    }

    @Test
    void findById_Success() {
        // Arrange
        Long id = 1L;
        Training training = new Training();
        training.setId(id);
        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));

        // Act
        Optional<Training> result = trainingService.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(trainingRepository).findById(id);
    }

    @Test
    void getTraineeTrainingsList_Success() {
        // Arrange
        String username = "Trainee.One";
        LocalDate from = LocalDate.now().minusDays(5);
        LocalDate to = LocalDate.now();
        String trainerName = "Trainer.One";
        String trainingType = "Cardio";

        Training training1 = new Training();
        Training training2 = new Training();
        List<Training> expectedTrainings = Arrays.asList(training1, training2);

        when(trainingRepository.findAll(Mockito.<Specification<Training>>any())).thenReturn(expectedTrainings);

        // Act
        List<Training> result = trainingService.getTraineeTrainingsList(username, from, to, trainerName, trainingType);

        // Assert
        assertEquals(2, result.size());
        verify(trainingRepository).findAll(Mockito.<Specification<Training>>any());
    }

    @Test
    void getTrainerTrainingsList_Success() {
        // Arrange
        String username = "Trainer.One";
        LocalDate from = LocalDate.now().minusDays(5);
        LocalDate to = LocalDate.now();
        String traineeName = "Trainee.One";
        String trainingType = "Strength";

        Training training1 = new Training();
        List<Training> expectedTrainings = List.of(training1);

        when(trainingRepository.findAll(Mockito.<Specification<Training>>any())).thenReturn(expectedTrainings);

        // Act
        List<Training> result = trainingService.getTrainerTrainingsList(username, from, to, traineeName, trainingType);

        // Assert
        assertEquals(1, result.size());
        verify(trainingRepository).findAll(Mockito.<Specification<Training>>any());
    }
}