package com.gym.crmspringboot.controller.api;

import com.gym.crmspringboot.dto.request.TrainerRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTrainerRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import com.gym.crmspringboot.dto.response.TrainerItemResponse;
import com.gym.crmspringboot.dto.response.TrainerProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Trainer Management", description = "Endpoints for managing gym trainers")
public interface TrainerApi {

    @Operation(summary = "Register a new Trainer", description = "Creates a new trainer profile and returns generated credentials.")
    @ApiResponse(responseCode = "201", description = "Trainer successfully created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    RegistrationResponse registerTrainer(TrainerRegistrationRequest request);

    @Operation(summary = "Get Trainer Profile", description = "Retrieves a trainer's profile by username.")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Trainer not found")
    TrainerProfileResponse getTrainerProfile(String username, String password);

    @Operation(summary = "Update Trainer Profile", description = "Updates an existing trainer's details.")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    TrainerProfileResponse updateTrainerProfile(String username, String password, UpdateTrainerRequest updateTrainerRequest);

    @Operation(summary = "Get Unassigned Trainers", description = "Retrieves a list of active trainers not currently assigned to the specified trainee.")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    List<TrainerItemResponse> getUnassignedTrainers(String username, String password, String traineeUsername);
}