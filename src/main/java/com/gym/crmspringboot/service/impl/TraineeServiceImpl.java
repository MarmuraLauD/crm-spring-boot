package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.repository.TraineeRepository;
import com.gym.crmspringboot.repository.TrainerRepository;
import com.gym.crmspringboot.service.TraineeService;
import com.gym.crmspringboot.service.helper.CredentialsService;
import com.gym.crmspringboot.service.security.RequireAuth;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        trainee.setPassword(password);

        String username = credentialsService.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName()
        );
        trainee.setUsername(username);
        trainee.setActive(true);
        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("Trainee profile created with username: {}", savedTrainee.getUsername());

        if (activeTraineesGauge != null) {
            activeTraineesGauge.incrementAndGet();
        }

        return savedTrainee;
    }

    @Override
    @RequireAuth
    @Transactional
    @Timed(value = "trainee_service.update.time", description = "Time taken to update trainee")
    public Trainee updateTrainee(String username, String password, Trainee trainee) {
        log.info("Updating trainee profile with username: {}", trainee.getUsername());
        return traineeRepository.save(trainee);
    }

    @Override
    @RequireAuth
    @Transactional
    @Timed(value = "trainee_service.delete.time", description = "Time taken to delete trainee")
    public void deleteTrainee(String username, String password) {
        log.info("Deleting trainee profile with username: {}", username);
        Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);

        if (traineeOpt.isPresent()) {
            traineeRepository.deleteByUsername(username);

            if (activeTraineesGauge != null) {
                activeTraineesGauge.decrementAndGet();
            }

        } else {
            throw new IllegalArgumentException("Trainee not found");
        }
    }

    @Override
    @RequireAuth
    @Transactional(readOnly = true)
    @Timed(value = "trainee_service.find.time", description = "Time taken to find trainee")
    public Trainee findByUsername(String username, String password) {
        log.info("Finding trainee profile with username: {}", username);
        return traineeRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Trainee not found"));
    }

    @Override
    @RequireAuth
    @Transactional
    @Timed(value = "trainee_service.update_trainers.time", description = "Time taken to update trainee's trainer list")
    public List<Trainer> updateTrainersList(String username, String password, List<String> trainerUsernames) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        List<Trainer> newTrainers = trainerRepository.findByUsernameIn(trainerUsernames);

        trainee.setTrainers(new HashSet<>(newTrainers));

        return new ArrayList<>(trainee.getTrainers());
    }

}
