package com.gym.crmspringboot.controller;

import com.gym.crmspringboot.controller.api.TrainingApi;
import com.gym.crmspringboot.dto.request.AddTrainingRequest;
import com.gym.crmspringboot.dto.response.TraineeTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainerTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainingTypeItemResponse;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.mapper.TrainingMapper;
import com.gym.crmspringboot.mapper.TrainingTypeMapper;
import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.model.TrainingType;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingController implements TrainingApi {

    private final GymFacade gymFacade;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    @GetMapping("/trainee/{username}")
    @PreAuthorize("hasRole('TRAINER')")
    public List<TraineeTrainingItemResponse> getTraineeTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType) {
        List<Training> trainings = gymFacade.getTraineeTrainingsList(
                username,
                periodFrom,
                periodTo,
                trainerName,
                trainingType
        );
        return trainings.stream()
                .map(trainingMapper::toTraineeTrainingItemResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/trainer/{username}")
    public List<TrainerTrainingItemResponse> getTrainerTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String traineeName,
            @RequestParam(required = false) String trainingType) {
        List<Training> trainings = gymFacade.getTrainerTrainingsList(
                username,
                periodFrom,
                periodTo,
                traineeName,
                trainingType);
        return trainings.stream()
                .map(trainingMapper::toTrainerTrainingItemResponse)
                .toList();
    }

    @Override
    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    public void addTraining(
            @RequestBody AddTrainingRequest trainingDto) {
        gymFacade.createTraining(trainingDto);
    }

    @Override
    @GetMapping("/types")
    @PreAuthorize("hasRole('TRAINER')")
    public List<TrainingTypeItemResponse> getTrainingTypes() {
        List<TrainingType> types = gymFacade.getAllTrainingTypes();
        return types.stream()
                .map(trainingTypeMapper::toItemResponse)
                .toList();
    }

}
