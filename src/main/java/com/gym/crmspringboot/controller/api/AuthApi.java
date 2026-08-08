package com.gym.crmspringboot.controller.api;

import com.gym.crmspringboot.dto.request.AuthRequest;
import com.gym.crmspringboot.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;

@Tag(name = "Authorization", description = "Endpoints for authorization and user account management")
public interface AuthApi {

    @Operation(summary = "User Login", description = "Validates user credentials via the authorization aspect.")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials")
    AuthResponse login(AuthRequest authRequest, HttpServletResponse response);

    @Operation(summary = "User Logout", description = "Logs out the user by invalidating the refresh token and clearing the cookie.")
    @ApiResponse(responseCode = "200", description = "Logout successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token")
    void logout(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response);

    @Operation(summary = "Refresh Access Token", description = "Generates a new access token using the refresh token.")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token")
    AuthResponse refresh(@CookieValue(name = "refresh_token") String refreshToken);

}
