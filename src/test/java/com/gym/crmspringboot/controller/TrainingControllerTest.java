package com.gym.crmspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crmspringboot.dto.request.AddTrainingRequest;
import com.gym.crmspringboot.dto.response.TraineeTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainerTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainingTypeItemResponse;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.mapper.TrainingMapper;
import com.gym.crmspringboot.mapper.TrainingTypeMapper;
import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.model.TrainingType;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private GymFacade gymFacade;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void getTraineeTrainings_ReturnsList() throws Exception {
        // Arrange
        String username = "Trainee.One";
        Training training = new Training();
        List<Training> trainings = List.of(training);

        TraineeTrainingItemResponse responseItem = TraineeTrainingItemResponse.builder()
                .trainingName("Yoga")
                .build();

        when(gymFacade.getTraineeTrainingsList(eq(username), any(), any(), any(), any())).thenReturn(trainings);
        when(trainingMapper.toTraineeTrainingItemResponse(training)).thenReturn(responseItem);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/trainings/trainee/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Yoga"));
    }

    @Test
    void getTrainerTrainings_ReturnsList() throws Exception {
        // Arrange
        String username = "Trainer.One";
        Training training = new Training();
        List<Training> trainings = List.of(training);

        TrainerTrainingItemResponse responseItem = TrainerTrainingItemResponse.builder()
                .trainingName("Cardio")
                .build();

        when(gymFacade.getTrainerTrainingsList(eq(username), any(), any(), any(), any())).thenReturn(trainings);
        when(trainingMapper.toTrainerTrainingItemResponse(training)).thenReturn(responseItem);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/trainings/trainer/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Cardio"));
    }

    @Test
    void addTraining_ReturnsStatusOk() throws Exception {
        // Arrange
        AddTrainingRequest request = AddTrainingRequest.builder()
                .trainingName("New Training")
                .build();
        Training training = new Training();

        when(trainingMapper.toEntity(any(AddTrainingRequest.class))).thenReturn(training);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).createTraining(training);
    }

    @Test
    void getTrainingTypes_ReturnsList() throws Exception {
        // Arrange
        TrainingType type = new TrainingType();
        List<TrainingType> types = List.of(type);

        TrainingTypeItemResponse itemResponse = TrainingTypeItemResponse.builder()
                .trainingType("Strength")
                .build();

        when(gymFacade.getAllTrainingTypes()).thenReturn(types);
        when(trainingTypeMapper.toItemResponse(type)).thenReturn(itemResponse);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/trainings/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingType").value("Strength"));
    }


}