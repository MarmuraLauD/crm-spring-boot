package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.client.TrainerWorkloadClient;
import com.gym.crmspringboot.dto.ActionType;
import com.gym.crmspringboot.dto.request.WorkloadRequest;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.repository.TrainingRepository;
import com.gym.crmspringboot.security.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainerWorkloadClient workloadClient;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void create_Success() {
        // Arrange
        Trainer trainer = new Trainer();
        trainer.setUsername("Trainer.One");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setActive(true);

        Training training = new Training();
        training.setName("Yoga Basics");
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60.0);

        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::extractAuthToken).thenReturn("Bearer token");

            // Act
            Training result = trainingService.create(training);

            // Assert
            assertEquals("Yoga Basics", result.getName());
            verify(trainingRepository).save(training);

            ArgumentCaptor<WorkloadRequest> requestCaptor = ArgumentCaptor.forClass(WorkloadRequest.class);
            verify(workloadClient).updateWorkload(requestCaptor.capture(), eq("Bearer token"));
            assertEquals(ActionType.ADD, requestCaptor.getValue().getActionType());
        }
    }

    @Test
    void delete_Success() {
        // Arrange
        Long id = 1L;
        Trainer trainer = new Trainer();
        trainer.setUsername("Trainer.One");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setActive(true);

        Training training = new Training();
        training.setId(id);
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60.0);

        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::extractAuthToken).thenReturn("Bearer token");

            // Act
            trainingService.delete(id);

            // Assert
            verify(trainingRepository).delete(training);

            ArgumentCaptor<WorkloadRequest> requestCaptor = ArgumentCaptor.forClass(WorkloadRequest.class);
            verify(workloadClient).updateWorkload(requestCaptor.capture(), eq("Bearer token"));
            assertEquals(ActionType.DELETE, requestCaptor.getValue().getActionType());
        }
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