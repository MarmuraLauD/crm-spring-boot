package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.Role;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.repository.TrainerRepository;
import com.gym.crmspringboot.service.TrainerService;
import com.gym.crmspringboot.service.helper.CredentialsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final CredentialsService credentialsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Trainer create(Trainer trainer) {
        log.info("Creating trainer profile for: {}",
                trainer.getFirstName() + " " + trainer.getLastName());

        String password = credentialsService.generatePassword();
        trainer.setPassword(passwordEncoder.encode(password));

        String username = credentialsService.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName()
        );
        trainer.setUsername(username);
        trainer.setActive(true);
        trainer.setRole(Role.ROLE_TRAINER);
        Trainer savedTrainer = trainerRepository.save(trainer);
        savedTrainer.setRawPassword(password);
        log.info("Trainer profile created with username: {}", savedTrainer.getUsername());

        return savedTrainer;
    }

    @Override
    @Transactional
    public Trainer update(Trainer trainer) {
        log.info("Updating trainer profile with username: {}", trainer.getUsername());
        return trainerRepository.save(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findByUsername(String username) {
        log.info("Finding trainer profile with username: {}", username);
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + username));
    }

    @Override
    @Transactional
    public List<Trainer> getUnassignedActiveTrainers(String traineeUsername) {
        log.info("Getting unassigned active trainers for username: {}", traineeUsername);
        return trainerRepository.getUnassignedActiveTrainers(traineeUsername);
    }

}
