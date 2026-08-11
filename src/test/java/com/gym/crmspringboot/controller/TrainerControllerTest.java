package com.gym.crmspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crmspringboot.dto.request.TrainerRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTrainerRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import com.gym.crmspringboot.dto.response.TrainerItemResponse;
import com.gym.crmspringboot.dto.response.TrainerProfileResponse;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.mapper.TrainerMapper;
import com.gym.crmspringboot.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private GymFacade gymFacade;

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainerController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerTrainer_ReturnsStatusCreated() throws Exception {
        // Arrange
        TrainerRegistrationRequest request = TrainerRegistrationRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .specializationId(1L)
                .build();

        Trainer trainer = new Trainer();
        Trainer createdTrainer = new Trainer();

        RegistrationResponse response = RegistrationResponse.builder()
                .username("trainerUsername")
                .password("trainerPassword")
                .build();

        when(trainerMapper.toEntity(any(TrainerRegistrationRequest.class))).thenReturn(trainer);
        when(gymFacade.registerTrainer(trainer)).thenReturn(createdTrainer);
        when(trainerMapper.toRegistrationResponse(createdTrainer)).thenReturn(response);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("trainerUsername"))
                .andExpect(jsonPath("$.password").value("trainerPassword"));
    }

    @Test
    void getTrainerProfile_ReturnsProfile() throws Exception {
        // Arrange
        String username = "Jane.Doe";
        Trainer trainer = new Trainer();

        TrainerProfileResponse response = TrainerProfileResponse.builder()
                .firstName("Jane")
                .lastName("Doe")
                .isActive(true)
                .build();

        when(gymFacade.getTrainerByUsername(username)).thenReturn(trainer);
        when(trainerMapper.toProfileResponse(trainer)).thenReturn(response);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/trainers/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void updateTrainerProfile_ReturnsUpdatedProfile() throws Exception {
        // Arrange
        String username = "Jane.Doe";

        UpdateTrainerRequest request = UpdateTrainerRequest.builder()
                .firstName("JaneUpdated")
                .lastName("Doe")
                .isActive(true)
                .build();

        Trainer trainer = new Trainer();
        Trainer updatedTrainer = new Trainer();

        TrainerProfileResponse response = TrainerProfileResponse.builder()
                .firstName("JaneUpdated")
                .lastName("Doe")
                .isActive(true)
                .build();

        when(trainerMapper.toEntity(any(UpdateTrainerRequest.class))).thenReturn(trainer);
        when(gymFacade.updateTrainer(trainer)).thenReturn(updatedTrainer);
        when(trainerMapper.toProfileResponse(updatedTrainer)).thenReturn(response);

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/trainers/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("JaneUpdated"));
    }

    @Test
    void getUnassignedTrainers_ReturnsList() throws Exception {
        // Arrange
        String traineeUsername = "Trainee.One";
        Trainer trainer = new Trainer();
        List<Trainer> unassignedTrainers = Arrays.asList(trainer);

        TrainerItemResponse itemResponse = TrainerItemResponse.builder()
                .username("Trainer.Unassigned")
                .firstName("Trainer")
                .lastName("Unassigned")
                .build();

        when(gymFacade.getUnassignedActiveTrainers(traineeUsername)).thenReturn(unassignedTrainers);
        when(trainerMapper.toItemResponse(trainer)).thenReturn(itemResponse);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/trainers/unassigned")
                        .param("traineeUsername", traineeUsername))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Trainer.Unassigned"));
    }
}