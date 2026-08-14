package com.gym.crmspringboot.service;

import com.gym.crmspringboot.model.Training;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingService {

    Training create(Training training);
    Optional<Training> findById(Long id);
    List<Training> getTraineeTrainingsList(
            String username,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingType
    );
    List<Training> getTrainerTrainingsList(
            String username,
            LocalDate from,
            LocalDate to,
            String traineeName,
            String trainingType
    );

}
