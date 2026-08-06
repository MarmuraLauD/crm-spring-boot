package com.gym.crmspringboot.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Management", description = "Endpoints for authentication and user account management")
public interface UserApi {

    @Operation(summary = "User Login", description = "Validates user credentials via the authorization aspect.")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials")
    void login(String username, String password);

    @Operation(summary = "Change Password", description = "Updates a user's password using their current credentials for validation.")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid old password")
    void changePassword(String username, String oldPassword, String newPassword);

    @Operation(summary = "Toggle Account Status", description = "Activates or deactivates a user's account.")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    void toggleActive(String username, Boolean status);
}