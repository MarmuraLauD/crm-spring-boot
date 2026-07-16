package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.repository.TraineeRepository;
import com.gym.crmspringboot.repository.TrainerRepository;
import com.gym.crmspringboot.service.TraineeService;
import com.gym.crmspringboot.service.helper.CredentialsService;
import com.gym.crmspringboot.service.security.RequireAuth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

    @Override
    @Transactional
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

        return savedTrainee;
    }

    @Override
    @RequireAuth
    @Transactional
    public Trainee updateTrainee(String username, String password, Trainee trainee) {
        log.info("Updating trainee profile with username: {}", trainee.getUsername());
        return traineeRepository.update(trainee);
    }

    @Override
    @RequireAuth
    @Transactional
    public void deleteTrainee(String username, String password) {
        log.info("Deleting trainee profile with username: {}", username);
        Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);

        if (traineeOpt.isPresent()) {
            traineeRepository.delete(username);
        } else {
            throw new IllegalArgumentException("Trainee not found");
        }
    }

    @Override
    @RequireAuth
    @Transactional(readOnly = true)
    public Optional<Trainee> findByUsername(String username, String password) {
        log.info("Finding trainee profile with username: {}", username);
        return traineeRepository.findByUsername(username);
    }

    @Override
    @RequireAuth
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for trainee with username: {}", username);

        Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);
        if (traineeOpt.isPresent()) {
            Trainee trainee = traineeOpt.get();
            trainee.setPassword(newPassword);
            log.info("Password changed successfully for trainee with username: {}", username);
        } else {
            log.warn("Trainee not found with username: {}", username);
            throw new IllegalArgumentException("Trainee not found with username: " + username);
        }
    }

    @Override
    @RequireAuth
    @Transactional
    public void toggleActive(String username, String password) {
        log.info("Toggling active status for trainee with username: {}", username);

        Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);
        if (traineeOpt.isPresent()) {
            Trainee trainee = traineeOpt.get();
            trainee.setActive(!trainee.isActive());
            log.info("Trainee with username: {} is now {}", username, trainee.isActive() ? "active" : "inactive");
        } else {
            log.warn("Trainee not found with username: {}", username);
            throw new IllegalArgumentException("Trainee not found with username: " + username);
        }
    }

    @Override
    @RequireAuth
    @Transactional
    public List<Trainer> updateTrainersList(String username, String password, List<String> trainerUsernames) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        List<Trainer> newTrainers = trainerRepository.findByUsernames(trainerUsernames);

        trainee.setTrainers(new HashSet<>(newTrainers));

        return new ArrayList<>(trainee.getTrainers());
    }

}
