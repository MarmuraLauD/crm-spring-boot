package com.gym.crmspringboot.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crmspringboot.dto.request.TraineeRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTraineeRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import com.gym.crmspringboot.dto.response.TraineeProfileResponse;
import com.gym.crmspringboot.model.Role;
import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.repository.TraineeRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TraineeManagementSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TraineeRepository traineeRepository;

    private TraineeRegistrationRequest registrationRequest;
    private UpdateTraineeRequest updateRequest;
    private ResultActions resultActions;

    @Given("a valid trainee registration request with first name {string}, last name {string}, date of birth {string}, and address {string}")
    public void aValidTraineeRegistrationRequest(String firstName, String lastName, String dob, String address) {
        registrationRequest = TraineeRegistrationRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.parse(dob))
                .address(address)
                .build();
    }

    @Given("a registered trainee user with username {string}")
    public void aRegisteredTraineeUserWithUsername(String username) {
        if (traineeRepository.findByUsername(username).isEmpty()) {
            Trainee trainee = Trainee.builder()
                    .username(username)
                    .firstName("TestFirst")
                    .lastName("TestLast")
                    .password("dummy_password")
                    .active(true)
                    .role(Role.ROLE_TRAINEE)
                    .build();
            traineeRepository.save(trainee);
        }
    }

    @Given("a valid update trainee request for {string}")
    public void aValidUpdateTraineeRequestFor(String username) {
        updateRequest = UpdateTraineeRequest.builder()
                .username(username)
                .firstName("UpdatedTraineeFirst")
                .lastName("UpdatedTraineeLast")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("New Address")
                .isActive(true)
                .build();
    }

    @When("a POST request is made for trainee registration to {string}")
    public void aPostRequestIsMadeForTraineeRegistrationTo(String endpoint) throws Exception {
        resultActions = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)));
    }

    @When("a PUT request is made for trainee update to {string}")
    public void aPutRequestIsMadeForTraineeUpdateTo(String endpoint) throws Exception {
        resultActions = mockMvc.perform(put(endpoint)
                .with(user("trainee_user").roles("TRAINEE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));
    }

    @Then("the trainee response status should be {int}")
    public void theTraineeResponseStatusShouldBe(int expectedStatus) throws Exception {
        resultActions.andExpect(status().is(expectedStatus));
    }

    @And("the trainee response should contain username and password")
    public void theTraineeResponseShouldContainUsernameAndPassword() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertNotNull(response.username());
        assertNotNull(response.password());
    }

    @And("the trainee response should reflect the updated profile")
    public void theTraineeResponseShouldReflectTheUpdatedProfile() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        TraineeProfileResponse response = objectMapper.readValue(responseBody, TraineeProfileResponse.class);

        assertEquals("UpdatedTraineeFirst", response.firstName());
        assertEquals("UpdatedTraineeLast", response.lastName());
    }
}