package com.gym.crmspringboot.controller.api;

import com.gym.crmspringboot.dto.request.AddTrainingRequest;
import com.gym.crmspringboot.dto.response.TraineeTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainerTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainingTypeItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Training Management", description = "Endpoints for managing and viewing trainings")
public interface TrainingApi {

    @Operation(summary = "Get Trainee Trainings", description = "Retrieves a filtered list of trainings for a specific trainee.")
    @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    List<TraineeTrainingItemResponse> getTraineeTrainings(String username, String password, LocalDate periodFrom, LocalDate periodTo, String trainerName, String trainingType);

    @Operation(summary = "Get Trainer Trainings", description = "Retrieves a filtered list of trainings for a specific trainer.")
    @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    List<TrainerTrainingItemResponse> getTrainerTrainings(String username, String password, LocalDate periodFrom, LocalDate periodTo, String traineeName, String trainingType);

    @Operation(summary = "Add Training", description = "Creates a new training session between a trainee and a trainer.")
    @ApiResponse(responseCode = "200", description = "Training added successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    void addTraining(String username, String password, AddTrainingRequest trainingDto);

    @Operation(summary = "Get Training Types", description = "Retrieves all available static training types.")
    @ApiResponse(responseCode = "200", description = "Training types retrieved successfully")
    List<TrainingTypeItemResponse> getTrainingTypes();
}