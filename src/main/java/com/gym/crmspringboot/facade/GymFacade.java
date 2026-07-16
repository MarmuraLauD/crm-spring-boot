package com.gym.crmspringboot.facade;

import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.service.TraineeService;
import com.gym.crmspringboot.service.TrainerService;
import com.gym.crmspringboot.service.TrainingService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public Trainee createTrainee(Trainee trainee) {
        return traineeService.createTrainee(trainee);
    }

    public Trainee updateTrainee(String username, String password, Trainee trainee) {
        return traineeService.updateTrainee(username, password, trainee);
    }

    public void deleteTrainee(String username, String password) {
        traineeService.deleteTrainee(username, password);
    }

    public Optional<Trainee> getTraineeByUsername(String username, String password) {
        return traineeService.findByUsername(username, password);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public void toggleTraineeActive(String username, String password) {
        traineeService.toggleActive(username, password);
    }

    public List<Trainer> updateTraineesTrainerList(
            String username,
            String password,
            List<String> trainerUsernames
    ) {
        return traineeService.updateTrainersList(username, password, trainerUsernames);
    }

    public Trainer createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Trainer updateTrainer(String username, String password, Trainer trainer) {
        return trainerService.update(username, password, trainer);
    }

    public Optional<Trainer> getTrainerByUsername(String username, String password) {
        return trainerService.findByUsername(username, password);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public void toggleTrainerActive(String username, String password) {
        trainerService.toggleActive(username, password);
    }

    public Training createTraining(String username, String password, Training training) {
        return trainingService.create(username, password, training);
    }

    public Optional<Training> getTrainingById(String username, String password, Long id) {
        return trainingService.findById(username, password, id);
    }

    public List<Training> getTraineeTrainingsList(
            String username,
            String password,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String typeName
    ) {
        return trainingService.getTraineeTrainingsList(
                username,
                password,
                from,
                to,
                trainerName,
                typeName
        );
    }

    public List<Training> getTrainerTrainingsList(
            String username,
            String password,
            LocalDate from,
            LocalDate to,
            String traineeName,
            String typeName
    ) {
        return trainingService.getTrainerTrainingsList(
                username,
                password,
                from,
                to,
                traineeName,
                typeName
        );
    }
}
