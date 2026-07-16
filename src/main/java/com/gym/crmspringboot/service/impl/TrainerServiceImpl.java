package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.repository.TrainerRepository;
import com.gym.crmspringboot.service.TrainerService;
import com.gym.crmspringboot.service.helper.CredentialsService;
import com.gym.crmspringboot.service.security.RequireAuth;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerDao;
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
        Trainer savedTrainer = trainerDao.save(trainer);
        log.info("Trainer profile created with username: {}", savedTrainer.getUsername());

        return savedTrainer;
    }

    @Override
    @Transactional
    @RequireAuth
    public Trainer update(String username, String password, Trainer trainer) {
        log.info("Updating trainer profile with username: {}", trainer.getUsername());
        return trainerDao.update(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    @RequireAuth
    public Optional<Trainer> findByUsername(String username, String password) {
        log.info("Finding trainer profile with username: {}", username);
        return trainerDao.findByUsername(username);
    }

    @Override
    @Transactional
    @RequireAuth
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for trainer with username: {}", username);
        Optional<Trainer> optionalTrainer = trainerDao.findByUsername(username);

        if (optionalTrainer.isPresent()) {
            Trainer trainer = optionalTrainer.get();
            if (credentialsService.authenticate(username, oldPassword)) {
                trainer.setPassword(newPassword);
                log.info("Password changed successfully for trainer with username: {}", username);
            } else {
                log.warn("Old password does not match for trainer with username: {}", username);
                throw new IllegalArgumentException("Old password does not match.");
            }
        } else {
            log.warn("Trainer with username {} not found.", username);
            throw new IllegalArgumentException("Trainer not found.");
        }
    }

    @Override
    @Transactional
    @RequireAuth
    public void toggleActive(String username, String password) {
        log.info("Toggling active status for trainer with username: {}", username);

        Optional<Trainer> trainerOpt = trainerDao.findByUsername(username);
        if (trainerOpt.isPresent()) {
            Trainer trainer = trainerOpt.get();
            trainer.setActive(!trainer.isActive());
            log.info("Trainer with username: {} is now {}", username, trainer.isActive() ? "active" : "inactive");
        } else {
            log.warn("Trainer with username {} not found.", username);
            throw new IllegalArgumentException("Trainer not found.");
        }
    }

}
