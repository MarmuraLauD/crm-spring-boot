package com.gym.crmspringboot.cucumber.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crmspringboot.dto.request.UpdateTrainerRequest;
import com.gym.crmspringboot.dto.response.TrainerItemResponse;
import com.gym.crmspringboot.dto.response.TrainerProfileResponse;
import com.gym.crmspringboot.model.Role;
import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.model.TrainingType;
import com.gym.crmspringboot.repository.TraineeRepository;
import com.gym.crmspringboot.repository.TrainerRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainerManagementSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    private ResultActions resultActions;
    private UpdateTrainerRequest updateRequest;

    @Given("a registered trainer user with username {string}")
    public void aRegisteredTrainerUserWithUsername(String username) {
        if (trainerRepository.findByUsername(username).isEmpty()) {
            TrainingType specialization = new TrainingType();
            specialization.setId(1L);

            Trainer trainer = Trainer.builder()
                    .username(username)
                    .firstName("TestFirst")
                    .lastName("TestLast")
                    .password("dummy_password")
                    .active(true)
                    .role(Role.ROLE_TRAINER)
                    .specialization(specialization)
                    .build();
            trainerRepository.save(trainer);
        }
    }

    @Given("a trainee user exists with username {string}")
    public void aTraineeUserExistsWithUsername(String traineeUsername) {
        if (traineeRepository.findByUsername(traineeUsername).isEmpty()) {
            Trainee trainee = Trainee.builder()
                    .username(traineeUsername)
                    .firstName("TraineeFirst")
                    .lastName("TraineeLast")
                    .password("dummy_password")
                    .active(true)
                    .role(Role.ROLE_TRAINEE)
                    .build();
            traineeRepository.save(trainee);
        }
    }

    @Given("a valid update trainer request for {string}")
    public void aValidUpdateTrainerRequestFor(String username) {
        updateRequest = UpdateTrainerRequest.builder()
                .username(username)
                .firstName("UpdatedName")
                .lastName("UpdatedLastName")
                .isActive(true)
                .build();
    }

    @Given("an invalid update trainer request for {string} with missing first name")
    public void anInvalidUpdateTrainerRequestForWithMissingFirstName(String username) {
        updateRequest = UpdateTrainerRequest.builder()
                .username(username)
                .firstName(null)
                .lastName("UpdatedLastName")
                .isActive(true)
                .build();
    }

    @When("a GET request is made by trainer for profile to {string}")
    public void aGetRequestIsMadeByTrainerForProfileTo(String endpoint) throws Exception {
        resultActions = mockMvc.perform(get(endpoint)
                .with(user("trainer_user").roles("TRAINER")));
    }

    @When("a PUT request is made by trainer for update to {string}")
    public void aPutRequestIsMadeByTrainerForUpdateTo(String endpoint) throws Exception {
        resultActions = mockMvc.perform(put(endpoint)
                .with(user("trainer_user").roles("TRAINER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));
    }

    @When("a GET request is made for unassigned trainers with traineeUsername {string}")
    public void aGetRequestIsMadeForUnassignedTrainersWithTraineeUsername(String traineeUsername) throws Exception {
        resultActions = mockMvc.perform(get("/api/v1/trainers/unassigned")
                .param("traineeUsername", traineeUsername)
                .with(user("trainer_user").roles("TRAINER")));
    }

    @Then("the management response status should be {int}")
    public void theManagementResponseStatusShouldBe(int expectedStatus) throws Exception {
        resultActions.andExpect(status().is(expectedStatus));
    }

    @And("the response should contain the trainer profile information")
    public void theResponseShouldContainTheTrainerProfileInformation() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        TrainerProfileResponse response = objectMapper.readValue(responseBody, TrainerProfileResponse.class);

        assertNotNull(response.firstName());
        assertNotNull(response.lastName());
    }

    @And("the response should reflect the updated trainer profile")
    public void theResponseShouldReflectTheUpdatedTrainerProfile() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        TrainerProfileResponse response = objectMapper.readValue(responseBody, TrainerProfileResponse.class);

        assertEquals("UpdatedName", response.firstName());
        assertEquals("UpdatedLastName", response.lastName());
    }

    @And("the response should be a list of trainers")
    public void theResponseShouldBeAListOfTrainers() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        List<TrainerItemResponse> response = objectMapper.readValue(responseBody, new TypeReference<>() {});

        assertNotNull(response);
    }
}