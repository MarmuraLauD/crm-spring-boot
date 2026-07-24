package com.gym.crmspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crmspringboot.dto.request.AddTrainingRequest;
import com.gym.crmspringboot.dto.response.TraineeTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainerTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainingTypeItemResponse;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.mapper.TrainingMapper;
import com.gym.crmspringboot.mapper.TrainingTypeMapper;
import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(TrainingController.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private GymFacade gymFacade;

    @MockitoBean
    private TrainingTypeMapper trainingTypeMapper;

    @MockitoBean
    private TrainingMapper trainingMapper;

    @Test
    void getTraineeTrainings_ShouldReturnOkStatusAndList() throws Exception {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        String periodFromStr = "2023-01-01";
        LocalDate periodFrom = LocalDate.of(2023, Month.JANUARY, 1);

        Training training = new Training();
        List<Training> trainings = List.of(training);

        TraineeTrainingItemResponse responseItem = TraineeTrainingItemResponse.builder()
                .trainingName("First Training")
                .trainingDate(LocalDate.of(2023, Month.JANUARY, 1))
                .trainingType("Cardio")
                .trainingDuration(120.0)
                .trainerName("Joe")
                .build();

        when(gymFacade.getTraineeTrainingsList(
                eq(username), eq(password), eq(periodFrom), isNull(), isNull(), isNull()
        )).thenReturn(trainings);
        when(trainingMapper.toTraineeTrainingItemResponse(training)).thenReturn(responseItem);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trainings/trainee/{username}", username)
                        .param("password", password)
                        .param("periodFrom", periodFromStr)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        verify(gymFacade).getTraineeTrainingsList(
                eq(username), eq(password), eq(periodFrom), isNull(), isNull(), isNull()
        );
    }

    @Test
    void getTrainerTrainings_ShouldReturnOkStatusAndList() throws Exception {
        // Arrange
        String username = "Jane.Smith";
        String password = "password123";
        String periodToStr = "2023-12-31";
        LocalDate periodTo = LocalDate.of(2023, Month.DECEMBER, 31);
        String trainingType = "Yoga";

        Training training = new Training();
        List<Training> trainings = List.of(training);

        TrainerTrainingItemResponse responseItem = TrainerTrainingItemResponse.builder()
                .trainingName("Second Training")
                .trainingDate(LocalDate.of(2023, Month.JANUARY, 1))
                .trainingType("Yoga")
                .trainingDuration(60.0)
                .traineeName("Jane")
                .build();

        when(gymFacade.getTrainerTrainingsList(
                eq(username), eq(password), isNull(), eq(periodTo), isNull(), eq(trainingType)
        )).thenReturn(trainings);
        when(trainingMapper.toTrainerTrainingItemResponse(training)).thenReturn(responseItem);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trainings/trainer/{username}", username)
                        .param("password", password)
                        .param("periodTo", periodToStr)
                        .param("trainingType", trainingType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        verify(gymFacade).getTrainerTrainingsList(
                eq(username), eq(password), isNull(), eq(periodTo), isNull(), eq(trainingType)
        );
    }

    @Test
    void addTraining_ShouldReturnOkStatus() throws Exception {
        // Arrange
        String username = "Admin.User";
        String password = "password123";

        AddTrainingRequest request = AddTrainingRequest.builder()
                .traineeUsername("Joe.Doe")
                .trainerUsername("Jane.Doe")
                .trainingName("First Training")
                .trainingDate(LocalDate.of(2023, Month.JANUARY, 1))
                .trainingDuration(120.0)
                .build();
        Training training = new Training();

        when(trainingMapper.toEntity(any(AddTrainingRequest.class))).thenReturn(training);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/trainings")
                        .param("username", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(trainingMapper).toEntity(any(AddTrainingRequest.class));
        verify(gymFacade).createTraining(eq(username), eq(password), any(Training.class));
    }

    @Test
    void getTrainingTypes_ShouldReturnOkStatusAndList() throws Exception {
        // Arrange
        TrainingType type1 = new TrainingType();
        type1.setId(1L);
        type1.setTrainingTypeName("Cardio");

        TrainingType type2 = new TrainingType();
        type2.setId(2L);
        type2.setTrainingTypeName("Strength");

        List<TrainingType> types = Arrays.asList(type1, type2);
        TrainingTypeItemResponse responseItem1 = TrainingTypeItemResponse.builder()
                .trainingType("Cardio")
                .build();
        TrainingTypeItemResponse responseItem2 = TrainingTypeItemResponse.builder()
                .trainingType("Strength")
                .build();

        when(trainingTypeMapper.toItemResponse(type1)).thenReturn(responseItem1);
        when(trainingTypeMapper.toItemResponse(type2)).thenReturn(responseItem2);
        when(gymFacade.getAllTrainingTypes()).thenReturn(types);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trainings/types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].trainingType").value("Cardio"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].trainingType").value("Strength"));

        verify(gymFacade).getAllTrainingTypes();
    }
}