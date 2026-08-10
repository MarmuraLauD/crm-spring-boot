package com.gym.crmspringboot.controller.api;

import com.gym.crmspringboot.dto.request.TraineeRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTraineeRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import com.gym.crmspringboot.dto.response.TraineeProfileResponse;
import com.gym.crmspringboot.dto.response.TrainerItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "Trainee Management", description = "Endpoints for managing gym trainees")
public interface TraineeApi {

    @Operation(summary = "Register a new Trainee", description = "Creates a new trainee profile and returns generated credentials.")
    @ApiResponse(responseCode = "201", description = "Trainee successfully created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    RegistrationResponse registerTrainee(TraineeRegistrationRequest request);

    @Operation(summary = "Get Trainee Profile", description = "Retrieves a trainee's profile by username.")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Trainee not found")
    TraineeProfileResponse getTraineeProfile(String username);

    @Operation(summary = "Update Trainee Profile", description = "Updates an existing trainee's details.")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    TraineeProfileResponse updateTraineeProfile(UpdateTraineeRequest updateTraineeRequest);

    @Operation(summary = "Delete Trainee Profile", description = "Deletes a trainee's profile completely by username.")
    @ApiResponse(responseCode = "200", description = "Profile deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    void deleteTraineeProfile(String username);

    @Operation(summary = "Update Trainee's Trainers", description = "Updates the list of trainers assigned to a specific trainee.")
    @ApiResponse(responseCode = "200", description = "Trainers updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    List<TrainerItemResponse> updateTraineeTrainers(String username, List<String> trainerUsernames);
}
