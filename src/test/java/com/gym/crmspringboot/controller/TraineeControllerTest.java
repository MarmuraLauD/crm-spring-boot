package com.gym.crmspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crmspringboot.dto.request.TraineeRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTraineeRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import com.gym.crmspringboot.dto.response.TraineeProfileResponse;
import com.gym.crmspringboot.dto.response.TrainerItemResponse;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.mapper.TraineeMapper;
import com.gym.crmspringboot.mapper.TrainerMapper;
import com.gym.crmspringboot.model.Trainee;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private GymFacade gymFacade;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TraineeController traineeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void registerTrainee_ReturnsStatusCreated() throws Exception {
        // Arrange
        TraineeRegistrationRequest request = TraineeRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        Trainee trainee = new Trainee();
        Trainee createdTrainee = new Trainee();

        RegistrationResponse response = RegistrationResponse.builder()
                .username("username")
                .password("password")
                .build();

        when(traineeMapper.toEntity(any(TraineeRegistrationRequest.class))).thenReturn(trainee);
        when(gymFacade.registerTrainee(trainee)).thenReturn(createdTrainee);
        when(traineeMapper.toRegistrationResponse(createdTrainee)).thenReturn(response);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.password").value("password"));
    }

    @Test
    void getTraineeProfile_ReturnsProfile() throws Exception {
        // Arrange
        String username = "John.Doe";
        Trainee trainee = new Trainee();

        TraineeProfileResponse response = TraineeProfileResponse.builder()
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .build();

        when(gymFacade.getTraineeByUsername(username)).thenReturn(trainee);
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(response);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/trainees/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void updateTraineeProfile_ReturnsUpdatedProfile() throws Exception {
        // Arrange
        String username = "John.Doe";

        UpdateTraineeRequest request = UpdateTraineeRequest.builder()
                .firstName("JohnUpdated")
                .lastName("Doe")
                .isActive(true)
                .build();

        Trainee trainee = new Trainee();
        Trainee updatedTrainee = new Trainee();

        TraineeProfileResponse response = TraineeProfileResponse.builder()
                .firstName("JohnUpdated")
                .lastName("Doe")
                .isActive(true)
                .build();

        when(traineeMapper.toEntity(any(UpdateTraineeRequest.class))).thenReturn(trainee);
        when(gymFacade.updateTrainee(trainee)).thenReturn(updatedTrainee);
        when(traineeMapper.toProfileResponse(updatedTrainee)).thenReturn(response);

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/trainees/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("JohnUpdated"));
    }

    @Test
    void deleteTraineeProfile_ReturnsStatusOk() throws Exception {
        // Arrange
        String username = "John.Doe";

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/trainees/{username}", username))
                .andExpect(status().isOk());

        verify(gymFacade).deleteTrainee(username);
    }

    @Test
    void updateTraineeTrainers_ReturnsUpdatedTrainersList() throws Exception {
        // Arrange
        String username = "John.Doe";
        List<String> trainerUsernames = List.of("Trainer.One");
        Trainer trainer = new Trainer();
        List<Trainer> updatedTrainers = List.of(trainer);

        TrainerItemResponse itemResponse = TrainerItemResponse.builder()
                .username("Trainer.One")
                .firstName("Trainer")
                .lastName("One")
                .build();

        when(gymFacade.updateTraineesTrainerList(eq(username), anyList())).thenReturn(updatedTrainers);
        when(trainerMapper.toItemResponse(trainer)).thenReturn(itemResponse);

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/trainees/{username}/trainers", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerUsernames)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Trainer.One"));
    }
}