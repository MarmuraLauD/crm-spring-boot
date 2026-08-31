package com.gym.crmspringboot.facade;

import com.gym.crmspringboot.dto.request.AddTrainingRequest;
import com.gym.crmspringboot.mapper.TrainingMapper;
import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.model.TrainingType;
import com.gym.crmspringboot.service.TraineeService;
import com.gym.crmspringboot.service.TrainerService;
import com.gym.crmspringboot.service.TrainingService;
import com.gym.crmspringboot.service.TrainingTypeService;
import com.gym.crmspringboot.service.UserService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final UserService userService;
    private final TrainingMapper trainingMapper;

    public Trainee registerTrainee(Trainee trainee) {
        return traineeService.createTrainee(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.updateTrainee(trainee);
    }

    public void deleteTrainee(String username) {
        traineeService.deleteTrainee(username);
    }

    public Trainee getTraineeByUsername(String username) {
        return traineeService.findByUsername(username);
    }

    public void changeUserPassword(String username, String oldPassword, String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
    }

    public void toggleUserActive(String username, Boolean status) {
        userService.toggleActive(username, status);
    }

    public List<Trainer> updateTraineesTrainerList(
            String username,
            List<String> trainerUsernames
    ) {
        return traineeService.updateTrainersList(username, trainerUsernames);
    }

    public Trainer registerTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.update(trainer);
    }

    public Trainer getTrainerByUsername(String username) {
        return trainerService.findByUsername(username);
    }

    public void createTraining(AddTrainingRequest trainingDto) {
        Trainee trainee = traineeService.findByUsername(trainingDto.traineeUsername());
        Trainer trainer = trainerService.findByUsername(trainingDto.trainerUsername());
        Training training = trainingMapper.toEntity(trainingDto, trainee, trainer);
        trainingService.create(training);
    }

    public List<Training> getTraineeTrainingsList(
            String username,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingType
    ) {
        return trainingService.getTraineeTrainingsList(
                username,
                from,
                to,
                trainerName,
                trainingType
        );
    }

    public List<Training> getTrainerTrainingsList(
            String username,
            LocalDate from,
            LocalDate to,
            String traineeName,
            String trainingType
    ) {
        return trainingService.getTrainerTrainingsList(
                username,
                from,
                to,
                traineeName,
                trainingType
        );
    }

    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeService.getAllTrainingTypes();
    }

    public List<Trainer> getUnassignedActiveTrainers(String username) {
        return trainerService.getUnassignedActiveTrainers(username);
    }

}
