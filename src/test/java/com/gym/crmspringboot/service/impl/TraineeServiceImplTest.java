package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.client.TrainerWorkloadClient;
import com.gym.crmspringboot.dto.ActionType;
import com.gym.crmspringboot.dto.request.WorkloadRequest;
import com.gym.crmspringboot.exception.UserNotFoundException;
import com.gym.crmspringboot.model.Role;
import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.repository.TraineeRepository;
import com.gym.crmspringboot.repository.TrainerRepository;
import com.gym.crmspringboot.security.util.SecurityUtils;
import com.gym.crmspringboot.service.helper.CredentialsService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private CredentialsService credentialsService;
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TrainerWorkloadClient workloadClient;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        when(traineeRepository.count()).thenReturn(5L);
        when(meterRegistry.gauge(anyString(), any(AtomicInteger.class)))
                .thenAnswer(invocation -> invocation.getArgument(1));

        traineeService.initMetrics();
    }

    @Test
    void createTrainee_Success() {
        // Arrange
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(credentialsService.generatePassword()).thenReturn("rawPassword123");
        when(passwordEncoder.encode("rawPassword123")).thenReturn("encodedPassword123");
        when(credentialsService.generateUsername("John", "Doe")).thenReturn("John.Doe");
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Trainee result = traineeService.createTrainee(trainee);

        // Assert
        assertEquals("John.Doe", result.getUsername());
        assertEquals("rawPassword123", result.getRawPassword());
        assertEquals("encodedPassword123", result.getPassword());
        assertTrue(result.isActive());
        assertEquals(Role.ROLE_TRAINEE, result.getRole());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTrainee_Success() {
        // Arrange
        Trainee trainee = new Trainee();
        trainee.setUsername("John.Doe");
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        // Act
        Trainee result = traineeService.updateTrainee(trainee);

        // Assert
        assertNotNull(result);
        assertEquals("John.Doe", result.getUsername());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void deleteTrainee_Success() {
        // Arrange
        String username = "John.Doe";
        Trainee trainee = new Trainee();
        trainee.setUsername(username);

        Trainer trainer = new Trainer();
        trainer.setUsername("Trainer.One");
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setActive(true);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(45.0);

        trainee.setTrainings(List.of(training));

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::extractAuthToken).thenReturn("Bearer token");

            // Act
            traineeService.deleteTrainee(username);

            // Assert
            verify(traineeRepository).deleteByUsername(username);

            ArgumentCaptor<WorkloadRequest> requestCaptor = ArgumentCaptor.forClass(WorkloadRequest.class);
            verify(workloadClient, times(1)).updateWorkload(requestCaptor.capture(), eq("Bearer token"));
            assertEquals(ActionType.DELETE, requestCaptor.getValue().getActionType());
        }
    }

    @Test
    void deleteTrainee_ThrowsException_WhenNotFound() {
        // Arrange
        String username = "Unknown.User";
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(UserNotFoundException.class, () -> traineeService.deleteTrainee(username));
    }

    @Test
    void findByUsername_Success() {
        // Arrange
        String username = "John.Doe";
        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        // Act
        Trainee result = traineeService.findByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void findByUsername_ThrowsException_WhenNotFound() {
        // Arrange
        String username = "Unknown.User";
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(UserNotFoundException.class, () -> traineeService.findByUsername(username));
    }

    @Test
    void updateTrainersList_Success() {
        // Arrange
        String username = "John.Doe";
        Trainee trainee = new Trainee();
        List<String> trainerUsernames = List.of("Trainer.One", "Trainer.Two");
        Trainer trainer1 = new Trainer();
        Trainer trainer2 = new Trainer();
        List<Trainer> newTrainers = List.of(trainer1, trainer2);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsernameIn(trainerUsernames)).thenReturn(newTrainers);

        // Act
        List<Trainer> result = traineeService.updateTrainersList(username, trainerUsernames);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(trainer1));
        assertTrue(result.contains(trainer2));
    }

    @Test
    void updateTrainersList_ThrowsException_WhenTraineeNotFound() {
        // Arrange
        String username = "Unknown.User";
        List<String> trainerUsernames = List.of("Trainer.One");
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(UserNotFoundException.class, () -> traineeService.updateTrainersList(username, trainerUsernames));
    }

}