package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.repository.TrainerRepository;
import com.gym.crmspringboot.service.TrainerService;
import com.gym.crmspringboot.service.helper.CredentialsService;
import com.gym.crmspringboot.service.security.RequireAuth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final CredentialsService credentialsService;

    @Override
    @Transactional
    public Trainer create(Trainer trainer) {
        log.info("Creating trainer profile for: {}",
                trainer.getFirstName() + " " + trainer.getLastName());

        String password = credentialsService.generatePassword();
        trainer.setPassword(password);

        String username = credentialsService.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName()
        );
        trainer.setUsername(username);
        trainer.setActive(true);
        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Trainer profile created with username: {}", savedTrainer.getUsername());

        return savedTrainer;
    }

    @Override
    @Transactional
    @RequireAuth
    public Trainer update(String username, String password, Trainer trainer) {
        log.info("Updating trainer profile with username: {}", trainer.getUsername());
        return trainerRepository.save(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    @RequireAuth
    public Trainer findByUsername(String username, String password) {
        log.info("Finding trainer profile with username: {}", username);
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + username));
    }

    @Override
    @Transactional
    @RequireAuth
    public List<Trainer> getUnassignedActiveTrainers(String username, String password, String traineeUsername) {
        log.info("Getting unassigned active trainers for username: {}", traineeUsername);
        return trainerRepository.getUnassignedActiveTrainers(traineeUsername);
    }

}
