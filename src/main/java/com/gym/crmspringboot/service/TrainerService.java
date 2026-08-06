package com.gym.crmspringboot.service;

import com.gym.crmspringboot.model.Trainer;
import java.util.List;

public interface TrainerService {

    Trainer create(Trainer trainer);
    Trainer update(Trainer trainer);
    Trainer findByUsername(String username);
    List<Trainer> getUnassignedActiveTrainers(String username);

}
