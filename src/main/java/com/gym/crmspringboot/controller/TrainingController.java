package com.gym.crmspringboot.controller;

import com.gym.crmspringboot.dto.request.AddTrainingRequest;
import com.gym.crmspringboot.dto.response.TraineeTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainerTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainingTypeItemResponse;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.mapper.TrainingMapper;
import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.model.TrainingType;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final GymFacade gymFacade;
    private final TrainingMapper trainingMapper;

    @GetMapping("/trainee/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<TraineeTrainingItemResponse> getTraineeTrainings(
            @PathVariable String username,
            @RequestParam String password,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType) {
        List<Training> trainings = gymFacade.getTraineeTrainingsList(
                username,
                password,
                periodFrom,
                periodTo,
                trainerName,
                trainingType
        );
        return trainings.stream()
                .map(trainingMapper::toTraineeTrainingItemResponse)
                .toList();
    }

    @GetMapping("/trainer/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<TrainerTrainingItemResponse> getTrainerTrainings(
            @PathVariable String username,
            @RequestParam String password,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String traineeName,
            @RequestParam(required = false) String trainingType) {
        List<Training> trainings = gymFacade.getTrainerTrainingsList(
                username,
                password,
                periodFrom,
                periodTo,
                traineeName,
                trainingType);
        return trainings.stream()
                .map(trainingMapper::toTrainerTrainingItemResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void addTraining(
            @RequestParam String username,
            @RequestParam String password,
            @RequestBody AddTrainingRequest trainingDto) {
        Training training = trainingMapper.toEntity(trainingDto);
        gymFacade.createTraining(username, password, training);
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public List<TrainingTypeItemResponse> getTrainingTypes() {
        List<TrainingType> types = gymFacade.getAllTrainingTypes();
        return types.stream()
                .map(type -> new TrainingTypeItemResponse(type.getId(), type.getTrainingTypeName()))
                .toList();
    }

}
