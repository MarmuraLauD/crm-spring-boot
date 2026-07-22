package com.gym.crmspringboot.service;

import com.gym.crmspringboot.model.Trainer;
import java.util.List;
import java.util.Optional;

public interface TrainerService {

    Trainer create(Trainer trainer);
    Trainer update(String username, String password, Trainer trainer);
    Optional<Trainer> findByUsername(String username, String password);
    List<Trainer> getUnassignedActiveTrainers(String username, String password, String traineeUsername);

}
