package com.gym.crmspringboot.controller;


import com.gym.crmspringboot.controller.api.TraineeApi;
import com.gym.crmspringboot.dto.request.TraineeRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTraineeRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import com.gym.crmspringboot.dto.response.TraineeProfileResponse;
import com.gym.crmspringboot.dto.response.TrainerItemResponse;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.mapper.TraineeMapper;
import com.gym.crmspringboot.mapper.TrainerMapper;
import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
public class TraineeController implements TraineeApi {

    private final GymFacade gymFacade;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse registerTrainee(@Valid @RequestBody TraineeRegistrationRequest request) {
        Trainee trainee = traineeMapper.toEntity(request);

        Trainee createdTrainee = gymFacade.registerTrainee(trainee);

        return traineeMapper.toRegistrationResponse(createdTrainee);
    }

    @Override
    @GetMapping("/{username}")
    public TraineeProfileResponse getTraineeProfile(
            @PathVariable String username,
            @RequestParam String password) {

        Trainee trainee = gymFacade.getTraineeByUsername(username, password);
        return traineeMapper.toProfileResponse(trainee);
    }

    @Override
    @PutMapping("/{username}")
    public TraineeProfileResponse updateTraineeProfile(
            @PathVariable String username,
            @RequestParam String password,
            @RequestBody UpdateTraineeRequest updateTraineeRequest)
 {
        Trainee trainee = traineeMapper.toEntity(updateTraineeRequest);
        Trainee updatedTrainee = gymFacade.updateTrainee(username, password, trainee);
        return traineeMapper.toProfileResponse(updatedTrainee);
    }

    @Override
    @DeleteMapping("/{username}")
    public void deleteTraineeProfile(
            @PathVariable String username,
            @RequestParam String password) {
        gymFacade.deleteTrainee(username, password);
    }

    @Override
    @PutMapping("/{username}/trainers")
    public List<TrainerItemResponse> updateTraineeTrainers(
            @PathVariable String username,
            @RequestParam String password,
            @RequestBody List<String> trainerUsernames) {
        List<Trainer> updatedTrainers = gymFacade
                .updateTraineesTrainerList(username, password, trainerUsernames);
        return updatedTrainers.stream()
                .map(trainerMapper::toItemResponse)
                .toList();
    }

}
