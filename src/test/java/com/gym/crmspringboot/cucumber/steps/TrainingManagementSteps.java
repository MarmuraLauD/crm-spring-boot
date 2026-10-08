package com.gym.crmspringboot.cucumber.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crmspringboot.dto.request.AddTrainingRequest;
import com.gym.crmspringboot.dto.response.TraineeTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainerTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainingTypeItemResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainingManagementSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions resultActions;
    private AddTrainingRequest addTrainingRequest;

    @Given("a valid add training request for trainee {string} and trainer {string}")
    public void aValidAddTrainingRequestForTraineeAndTrainer(String traineeUsername, String trainerUsername) {
        addTrainingRequest = AddTrainingRequest.builder()
                .traineeUsername(traineeUsername)
                .trainerUsername(trainerUsername)
                .trainingName("Morning Yoga")
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDuration(60.0)
                .build();
    }

    @Given("an invalid add training request with missing trainee username and trainer {string}")
    public void anInvalidAddTrainingRequestWithMissingTraineeUsernameAndTrainer(String trainerUsername) {
        addTrainingRequest = AddTrainingRequest.builder()
                .traineeUsername(null)
                .trainerUsername(trainerUsername)
                .trainingName("Morning Yoga")
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDuration(60.0)
                .build();
    }

    @When("a GET request is made by trainer to get training types at {string}")
    public void aGetRequestIsMadeByTrainerToGetTrainingTypesAt(String endpoint) throws Exception {
        resultActions = mockMvc.perform(get(endpoint)
                .with(user("trainer_user").roles("TRAINER")));
    }

    @When("a POST request is made by trainer to add training at {string}")
    public void aPostRequestIsMadeByTrainerToAddTrainingAt(String endpoint) throws Exception {
        resultActions = mockMvc.perform(post(endpoint)
                .with(user("trainer_user").roles("TRAINER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addTrainingRequest)));
    }

    @When("a GET request is made by trainer to get trainee trainings at {string}")
    public void aGetRequestIsMadeByTrainerToGetTraineeTrainingsAt(String endpoint) throws Exception {
        resultActions = mockMvc.perform(get(endpoint)
                .with(user("trainer_user").roles("TRAINER")));
    }

    @When("a GET request is made by trainer to get trainer trainings at {string}")
    public void aGetRequestIsMadeByTrainerToGetTrainerTrainingsAt(String endpoint) throws Exception {
        resultActions = mockMvc.perform(get(endpoint)
                .with(user("trainer_user").roles("TRAINER")));
    }

    @Then("the training response status should be {int}")
    public void theTrainingResponseStatusShouldBe(int expectedStatus) throws Exception {
        resultActions.andExpect(status().is(expectedStatus));
    }

    @And("the response should contain a list of training types")
    public void theResponseShouldContainAListOfTrainingTypes() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        List<TrainingTypeItemResponse> response = objectMapper.readValue(responseBody, new TypeReference<>() {});
        assertNotNull(response);
    }

    @And("the response should be a list of trainee trainings")
    public void theResponseShouldBeAListOfTraineeTrainings() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        List<TraineeTrainingItemResponse> response = objectMapper.readValue(responseBody, new TypeReference<>() {});
        assertNotNull(response);
    }

    @And("the response should be a list of trainer trainings")
    public void theResponseShouldBeAListOfTrainerTrainings() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        List<TrainerTrainingItemResponse> response = objectMapper.readValue(responseBody, new TypeReference<>() {});
        assertNotNull(response);
    }
}