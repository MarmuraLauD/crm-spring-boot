package com.gym.crmspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crmspringboot.dto.request.TraineeRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTraineeRequest;
import com.gym.crmspringboot.dto.response.TraineeProfileResponse;
import com.gym.crmspringboot.dto.response.TrainerItemResponse;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.mapper.TraineeMapper;
import com.gym.crmspringboot.mapper.TrainerMapper;
import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private GymFacade gymFacade;

    @MockitoBean
    private TraineeMapper traineeMapper;

    @MockitoBean
    private TrainerMapper trainerMapper;

    @Test
    void registerTrainee_ShouldReturnCreatedStatusAndCredentials() throws Exception {
        // Arrange
        TraineeRegistrationRequest request = TraineeRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1))
                .address("123 Main St")
                .build();
        Trainee trainee = new Trainee();
        Trainee createdTrainee = new Trainee();
        createdTrainee.setUsername("John.Doe");
        createdTrainee.setPassword("password123");

        when(traineeMapper.toEntity(any(TraineeRegistrationRequest.class))).thenReturn(trainee);
        when(gymFacade.registerTrainee(trainee)).thenReturn(createdTrainee);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("John.Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password123"));

        verify(traineeMapper).toEntity(any(TraineeRegistrationRequest.class));
        verify(gymFacade).registerTrainee(trainee);
    }

    @Test
    void getTraineeProfile_ShouldReturnOkStatusAndProfile() throws Exception {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        Trainee trainee = new Trainee();
        TraineeProfileResponse profileResponse = TraineeProfileResponse.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1))
                .address("123 Main St")
                .isActive(true)
                .trainers(List.of())
                .build();

        when(gymFacade.getTraineeByUsername(username, password)).thenReturn(trainee);
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(profileResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trainees/{username}", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(gymFacade).getTraineeByUsername(username, password);
        verify(traineeMapper).toProfileResponse(trainee);
    }

    @Test
    void updateTraineeProfile_ShouldReturnOkStatusAndUpdatedProfile() throws Exception {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        UpdateTraineeRequest request = UpdateTraineeRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1))
                .address("123 Main St")
                .isActive(true)
                .build();
        Trainee trainee = new Trainee();
        Trainee updatedTrainee = new Trainee();
        TraineeProfileResponse profileResponse = TraineeProfileResponse.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1))
                .address("123 Main St")
                .isActive(true)
                .trainers(List.of())
                .build();

        when(traineeMapper.toEntity(any(UpdateTraineeRequest.class))).thenReturn(trainee);
        when(gymFacade.updateTrainee(eq(username), eq(password), any(Trainee.class))).thenReturn(updatedTrainee);
        when(traineeMapper.toProfileResponse(updatedTrainee)).thenReturn(profileResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/trainees/{username}", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(traineeMapper).toEntity(any(UpdateTraineeRequest.class));
        verify(gymFacade).updateTrainee(eq(username), eq(password), any(Trainee.class));
        verify(traineeMapper).toProfileResponse(updatedTrainee);
    }

    @Test
    void deleteTraineeProfile_ShouldReturnOkStatus() throws Exception {
        // Arrange
        String username = "John.Doe";
        String password = "password123";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/trainees/{username}", username)
                        .param("password", password))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(gymFacade).deleteTrainee(username, password);
    }

    @Test
    void updateTraineeTrainers_ShouldReturnOkStatusAndTrainerList() throws Exception {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        List<String> trainerUsernames = Arrays.asList("Trainer.One", "Trainer.Two");

        Trainer trainer1 = new Trainer();
        Trainer trainer2 = new Trainer();
        List<Trainer> updatedTrainers = Arrays.asList(trainer1, trainer2);

        TrainerItemResponse itemResponse1 = TrainerItemResponse.builder()
                .username("Trainer.One")
                .firstName("John")
                .lastName("Doe")
                .specialization("CARDIO")
                .build();
        TrainerItemResponse itemResponse2 = TrainerItemResponse.builder()
                .username("Trainer.Two")
                .firstName("Jane")
                .lastName("Smith")
                .specialization("STRENGTH")
                .build();

        when(gymFacade.updateTraineesTrainerList(eq(username), eq(password), ArgumentMatchers.any())).thenReturn(updatedTrainers);
        when(trainerMapper.toItemResponse(trainer1)).thenReturn(itemResponse1);
        when(trainerMapper.toItemResponse(trainer2)).thenReturn(itemResponse2);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/trainees/{username}/trainers", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerUsernames)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

        verify(gymFacade).updateTraineesTrainerList(eq(username), eq(password), ArgumentMatchers.any());
    }
}