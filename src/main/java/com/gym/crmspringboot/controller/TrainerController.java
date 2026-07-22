package com.gym.crmspringboot.controller;

import com.gym.crmspringboot.dto.request.TrainerRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTrainerRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import com.gym.crmspringboot.dto.response.TrainerItemResponse;
import com.gym.crmspringboot.dto.response.TrainerProfileResponse;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.mapper.TrainerMapper;
import com.gym.crmspringboot.model.Trainer;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerMapper trainerMapper;
    private final GymFacade gymFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse registerTrainer(@Valid @RequestBody TrainerRegistrationRequest request) {
        Trainer trainer = trainerMapper.toEntity(request);
        Trainer createdTrainer = gymFacade.registerTrainer(trainer);
        return new RegistrationResponse(createdTrainer.getUsername(), createdTrainer.getPassword());
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public TrainerProfileResponse getTrainerProfile(
            @PathVariable String username,
            @RequestParam String password) {

        Trainer trainer = gymFacade.getTrainerByUsername(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found."));
        return trainerMapper.toProfileResponse(trainer);
    }

    @PutMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public TrainerProfileResponse updateTrainerProfile(
            @PathVariable String username,
            @RequestParam String password,
            @RequestBody UpdateTrainerRequest updateTrainerRequest) {

        Trainer trainer = trainerMapper.toEntity(updateTrainerRequest);
        Trainer updatedTrainer = gymFacade.updateTrainer(username, password, trainer);
        return trainerMapper.toProfileResponse(updatedTrainer);
    }

    @GetMapping("/unassigned")
    @ResponseStatus(HttpStatus.OK)
    public List<TrainerItemResponse> getUnassignedTrainers(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String traineeUsername) {
        List<Trainer> unassignedTrainers = gymFacade.getUnassignedActiveTrainers(username, password, traineeUsername);
        return unassignedTrainers.stream()
                .map(trainerMapper::toItemResponse)
                .toList();
    }

}
