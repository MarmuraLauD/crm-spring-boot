package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.client.TrainerWorkloadClient;
import com.gym.crmspringboot.dto.ActionType;
import com.gym.crmspringboot.dto.request.WorkloadRequest;
import com.gym.crmspringboot.exception.UserNotFoundException;
import com.gym.crmspringboot.model.Role;
import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.repository.TraineeRepository;
import com.gym.crmspringboot.repository.TrainerRepository;
import com.gym.crmspringboot.security.util.SecurityUtils;
import com.gym.crmspringboot.service.TraineeService;
import com.gym.crmspringboot.service.helper.CredentialsService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final CredentialsService credentialsService;
    private final MeterRegistry meterRegistry;
    private AtomicInteger activeTraineesGauge;
    private final PasswordEncoder passwordEncoder;
    private final TrainerWorkloadClient workloadClient;

    @PostConstruct
    public void initMetrics() {
        int currentCount = (int) traineeRepository.count();

        this.activeTraineesGauge = meterRegistry.gauge(
                "crm_active_trainees_total",
                new AtomicInteger(currentCount)
        );
    }

    @Override
    @Transactional
    @Timed(value = "trainee_service.create.time", description = "Time taken to create a new trainee")
    public Trainee createTrainee(Trainee trainee) {
        log.info("Creating trainee profile for: {}",
                trainee.getFirstName() + " " + trainee.getLastName());

        String password = credentialsService.generatePassword();
        trainee.setPassword(passwordEncoder.encode(password));

        String username = credentialsService.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName()
        );
        trainee.setUsername(username);
        trainee.setActive(true);
        trainee.setRole(Role.ROLE_TRAINEE);
        Trainee savedTrainee = traineeRepository.save(trainee);
        savedTrainee.setRawPassword(password);
        log.info("Trainee profile created with username: {}", savedTrainee.getUsername());

        if (activeTraineesGauge != null) {
            activeTraineesGauge.incrementAndGet();
        }

        return savedTrainee;
    }

    @Override
    @Transactional
    @Timed(value = "trainee_service.update.time", description = "Time taken to update trainee")
    public Trainee updateTrainee(Trainee trainee) {
        log.info("Updating trainee profile with username: {}", trainee.getUsername());
        return traineeRepository.save(trainee);
    }

    @Override
    @Transactional
    @Timed(value = "trainee_service.delete.time", description = "Time taken to delete trainee")
    public void deleteTrainee(String username) {
        log.info("Deleting trainee profile with username: {}", username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        String token = SecurityUtils.extractAuthToken();

        if (trainee.getTrainings() != null) {
            trainee.getTrainings().forEach(training -> {
                WorkloadRequest request = WorkloadRequest.builder()
                        .trainerUsername(training.getTrainer().getUsername())
                        .trainerFirstName(training.getTrainer().getFirstName())
                        .trainerLastName(training.getTrainer().getLastName())
                        .isActive(training.getTrainer().isActive())
                        .trainingDate(training.getTrainingDate())
                        .trainingDuration(training.getTrainingDuration().intValue())
                        .actionType(ActionType.DELETE)
                        .build();
                workloadClient.updateWorkload(request, token);
            });
        }

        traineeRepository.deleteByUsername(username);

        if (activeTraineesGauge != null) {
            activeTraineesGauge.decrementAndGet();
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Timed(value = "trainee_service.find.time", description = "Time taken to find trainee")
    public Trainee findByUsername(String username) {
        log.info("Finding trainee profile with username: {}", username);
        return traineeRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Trainee not found"));
    }

    @Override
    @Transactional
    @Timed(value = "trainee_service.update_trainers.time", description = "Time taken to update trainee's trainer list")
    public List<Trainer> updateTrainersList(String username, List<String> trainerUsernames) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        List<Trainer> newTrainers = trainerRepository.findByUsernameIn(trainerUsernames);

        trainee.setTrainers(new HashSet<>(newTrainers));

        return new ArrayList<>(trainee.getTrainers());
    }

}