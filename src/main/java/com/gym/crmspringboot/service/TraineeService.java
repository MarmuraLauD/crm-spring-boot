package com.gym.crmspringboot.service;

import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import java.util.List;


public interface TraineeService {

    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(Trainee trainee);
    void deleteTrainee(String username);
    Trainee findByUsername(String username);
    List<Trainer> updateTrainersList(String username, List<String> trainerUsernames);

}
