package com.gym.crmspringboot.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TestController testController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(testController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleUserNotFoundException_ReturnsNotFoundStatus() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void handleLockedException_ReturnsLockedStatus() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/test/locked"))
                .andExpect(status().is(HttpStatus.LOCKED.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.LOCKED.value()))
                .andExpect(jsonPath("$.message").value("Account is locked"));
    }

    @Test
    void handleBadCredentialsException_ReturnsUnauthorizedStatus() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void handleInvalidTokenException_ReturnsUnauthorizedStatus() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/test/invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").value("Token expired"));
    }

    @Test
    void handleIllegalArgumentException_ReturnsUnauthorizedStatus() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").value("Bad argument"));
    }

    @Test
    void handleGenericException_ReturnsInternalServerErrorStatus() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/test/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    @RestController
    static class TestController {
        @GetMapping("/test/not-found")
        public void throwNotFound() { throw new UserNotFoundException("User not found"); }

        @GetMapping("/test/locked")
        public void throwLocked() { throw new LockedException("Account is locked"); }

        @GetMapping("/test/bad-credentials")
        public void throwBadCredentials() { throw new BadCredentialsException("Bad credentials"); }

        @GetMapping("/test/invalid-token")
        public void throwInvalidToken() { throw new InvalidTokenException("Token expired"); }

        @GetMapping("/test/illegal-argument")
        public void throwIllegalArgument() { throw new IllegalArgumentException("Bad argument"); }

        @GetMapping("/test/generic-error")
        public void throwGeneric() { throw new RuntimeException("Unexpected"); }
    }
}