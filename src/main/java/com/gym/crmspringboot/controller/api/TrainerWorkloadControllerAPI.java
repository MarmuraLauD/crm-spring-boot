package com.gym.crmspringboot.controller.api;

import com.gym.crmspringboot.dto.response.TrainerWorkloadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Trainer Workload", description = "API for managing and retrieving trainer workloads")
public interface TrainerWorkloadControllerAPI {

    @Operation(summary = "Get trainer workload", description = "Retrieves the monthly and yearly workload summary for a specific trainer")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerWorkloadResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request or missing data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "503", description = "Service Unavailable (Fallback applied)")
    TrainerWorkloadResponse getTrainerWorkload(
            @Parameter(description = "Username of the trainer", required = true)
            String username
    );
}