package com.gym.crmspringboot.service;

import com.gym.crmspringboot.model.Trainer;
import java.util.List;

public interface TrainerService {

    Trainer create(Trainer trainer);
    Trainer update(String username, String password, Trainer trainer);
    Trainer findByUsername(String username, String password);
    List<Trainer> getUnassignedActiveTrainers(String username, String password, String traineeUsername);

}
