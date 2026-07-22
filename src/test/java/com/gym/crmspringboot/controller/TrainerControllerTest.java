package com.gym.crmspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crmspringboot.dto.request.TrainerRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTrainerRequest;
import com.gym.crmspringboot.dto.response.TrainerItemResponse;
import com.gym.crmspringboot.dto.response.TrainerProfileResponse;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.mapper.TrainerMapper;
import com.gym.crmspringboot.model.Trainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private TrainerMapper trainerMapper;

    @MockitoBean
    private GymFacade gymFacade;

    @Test
    void registerTrainer_ShouldReturnCreatedStatusAndCredentials() throws Exception {
        // Arrange

        TrainerRegistrationRequest request = new TrainerRegistrationRequest(
                "Jane",
                "Smith",
                2L
        );
        Trainer trainer = new Trainer();
        Trainer createdTrainer = new Trainer();
        createdTrainer.setUsername("Jane.Smith");
        createdTrainer.setPassword("password123");

        when(trainerMapper.toEntity(any(TrainerRegistrationRequest.class))).thenReturn(trainer);
        when(gymFacade.registerTrainer(trainer)).thenReturn(createdTrainer);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Jane.Smith"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password123"));

        verify(trainerMapper).toEntity(any(TrainerRegistrationRequest.class));
        verify(gymFacade).registerTrainer(trainer);
    }

    @Test
    void getTrainerProfile_ShouldReturnOkStatusAndProfile() throws Exception {
        // Arrange
        String username = "Jane.Smith";
        String password = "password123";
        Trainer trainer = new Trainer();

        TrainerProfileResponse profileResponse = new TrainerProfileResponse(
                "Jane",
                "Smith",
                "STRENGTH",
                true,
                List.of()
        );

        when(gymFacade.getTrainerByUsername(username, password)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toProfileResponse(trainer)).thenReturn(profileResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trainers/{username}", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(gymFacade).getTrainerByUsername(username, password);
        verify(trainerMapper).toProfileResponse(trainer);
    }

    @Test
    void updateTrainerProfile_ShouldReturnOkStatusAndUpdatedProfile() throws Exception {
        // Arrange
        String username = "Jane.Smith";
        String password = "password123";

        UpdateTrainerRequest request = new UpdateTrainerRequest(
                username,
                "Jane",
                "Doe",
                "CARDIO",
                true
        );
        Trainer trainer = new Trainer();
        Trainer updatedTrainer = new Trainer();

        
        TrainerProfileResponse profileResponse = new TrainerProfileResponse(
                "Jane",
                "Doe",
                "CARDIO",
                true,
                List.of()
        );

        when(trainerMapper.toEntity(any(UpdateTrainerRequest.class))).thenReturn(trainer);
        when(gymFacade.updateTrainer(eq(username), eq(password), any(Trainer.class))).thenReturn(updatedTrainer);
        when(trainerMapper.toProfileResponse(updatedTrainer)).thenReturn(profileResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/trainers/{username}", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(trainerMapper).toEntity(any(UpdateTrainerRequest.class));
        verify(gymFacade).updateTrainer(eq(username), eq(password), any(Trainer.class));
        verify(trainerMapper).toProfileResponse(updatedTrainer);
    }

    @Test
    void getUnassignedTrainers_ShouldReturnOkStatusAndTrainerList() throws Exception {
        // Arrange
        String username = "Admin.User";
        String password = "password123";
        String traineeUsername = "John.Doe";

        Trainer trainer1 = new Trainer();
        Trainer trainer2 = new Trainer();
        List<Trainer> unassignedTrainers = Arrays.asList(trainer1, trainer2);

        
        TrainerItemResponse itemResponse1 = new TrainerItemResponse(
                username,
                "Jane",
                "Doe",
                "STRENGTH"
        );
        TrainerItemResponse itemResponse2 = new TrainerItemResponse(
                username,
                "Jane",
                "Noes",
                "CARDIO"
        );

        when(gymFacade.getUnassignedActiveTrainers(username, password, traineeUsername)).thenReturn(unassignedTrainers);
        when(trainerMapper.toItemResponse(trainer1)).thenReturn(itemResponse1);
        when(trainerMapper.toItemResponse(trainer2)).thenReturn(itemResponse2);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trainers/unassigned")
                        .param("username", username)
                        .param("password", password)
                        .param("traineeUsername", traineeUsername)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

        verify(gymFacade).getUnassignedActiveTrainers(username, password, traineeUsername);
    }
}