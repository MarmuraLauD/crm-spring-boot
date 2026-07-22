package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void create_ShouldSaveTraining() {
        // Arrange
        String username = "Admin.User";
        String password = "password123";
        Training training = new Training();
        training.setName("Cardio Basics");

        when(trainingRepository.save(training)).thenReturn(training);

        // Act
        Training savedTraining = trainingService.create(username, password, training);

        // Assert
        assertEquals(training, savedTraining);
        verify(trainingRepository).save(training);
    }

    @Test
    void findById_ShouldReturnOptionalTraining() {
        // Arrange
        String username = "Admin.User";
        String password = "password123";
        Long trainingId = 1L;
        Training training = new Training();
        training.setId(trainingId);

        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

        // Act
        Optional<Training> result = trainingService.findById(username, password, trainingId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(trainingId, result.get().getId());
        verify(trainingRepository).findById(trainingId);
    }

    @Test
    void getTraineeTrainingsList_ShouldReturnTrainingsBasedOnSpecification() {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        LocalDate fromDate = LocalDate.of(2023, Month.JANUARY, 1);
        LocalDate toDate = LocalDate.of(2023, Month.DECEMBER, 31);
        String trainerName = "Jane.Smith";
        String trainingType = "Yoga";

        Training training1 = new Training();
        Training training2 = new Training();
        List<Training> expectedTrainings = Arrays.asList(training1, training2);

        when(trainingRepository.findAll(ArgumentMatchers.<Specification<Training>>any())).thenReturn(expectedTrainings);

        // Act
        List<Training> actualTrainings = trainingService.getTraineeTrainingsList(
                username, password, fromDate, toDate, trainerName, trainingType
        );

        // Assert
        assertEquals(expectedTrainings.size(), actualTrainings.size());
        assertEquals(expectedTrainings, actualTrainings);
        verify(trainingRepository).findAll(ArgumentMatchers.<Specification<Training>>any());
    }

    @Test
    void getTrainerTrainingsList_ShouldReturnTrainingsBasedOnSpecification() {
        // Arrange
        String username = "Jane.Smith";
        String password = "password123";
        LocalDate fromDate = LocalDate.of(2023, Month.JANUARY, 1);
        LocalDate toDate = LocalDate.of(2023, Month.DECEMBER, 31);
        String traineeName = "John.Doe";
        String trainingType = "Fitness";

        Training training1 = new Training();
        List<Training> expectedTrainings = List.of(training1);

        when(trainingRepository.findAll(ArgumentMatchers.<Specification<Training>>any())).thenReturn(expectedTrainings);

        // Act
        List<Training> actualTrainings = trainingService.getTrainerTrainingsList(
                username, password, fromDate, toDate, traineeName, trainingType
        );

        // Assert
        assertEquals(expectedTrainings.size(), actualTrainings.size());
        assertEquals(expectedTrainings, actualTrainings);
        verify(trainingRepository).findAll(ArgumentMatchers.<Specification<Training>>any());
    }
}