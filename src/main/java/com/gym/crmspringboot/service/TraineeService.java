package com.gym.crmspringboot.service;

import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import java.util.List;
import java.util.Optional;


public interface TraineeService {

    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(String username, String password, Trainee trainee);
    void deleteTrainee(String username, String password);
    Optional<Trainee> findByUsername(String username, String password);
    List<Trainer> updateTrainersList(String username, String password, List<String> trainerUsernames);

}
