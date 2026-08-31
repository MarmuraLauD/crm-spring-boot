package com.gym.crmspringboot.controller;

import com.gym.crmspringboot.client.TrainerWorkloadClient;
import com.gym.crmspringboot.dto.response.TrainerWorkloadResponse;
import com.gym.crmspringboot.security.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TrainerWorkloadClient workloadClient;

    @InjectMocks
    private TrainerWorkloadController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getTrainerWorkload_Success() throws Exception {
        // Arrange
        String username = "Trainer.One";
        TrainerWorkloadResponse response = new TrainerWorkloadResponse();
        response.setTrainerUsername(username);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::extractAuthToken).thenReturn("Bearer token");
            when(workloadClient.getTrainerWorkload(username, "Bearer token")).thenReturn(response);

            // Act
            // Assert
            mockMvc.perform(get("/api/v1/workloads/{username}", username))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.trainerUsername").value(username));
        }
    }
}