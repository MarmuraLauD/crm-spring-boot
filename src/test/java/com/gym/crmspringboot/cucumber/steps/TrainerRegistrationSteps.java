package com.gym.crmspringboot.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crmspringboot.dto.request.TrainerRegistrationRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainerRegistrationSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TrainerRegistrationRequest request;
    private ResultActions resultActions;

    @Given("a valid trainer registration request with first name {string}, last name {string}, and specialization ID {int}")
    public void aValidTrainerRegistrationRequest(String firstName, String lastName, int specId) {
        request = TrainerRegistrationRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specializationId((long) specId)
                .build();
    }

    @Given("an invalid trainer registration request with missing first name, last name {string}, and specialization ID {int}")
    public void anInvalidTrainerRegistrationRequest(String lastName, int specId) {
        request = TrainerRegistrationRequest.builder()
                .firstName(null)
                .lastName(lastName)
                .specializationId((long) specId)
                .build();
    }

    @When("a POST request is made for trainer registration to {string}")
    public void aPostRequestIsMadeForTrainerRegistrationTo(String endpoint) throws Exception {
        resultActions = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Then("the trainer registration response status should be {int}")
    public void theTrainerRegistrationResponseStatusShouldBe(int expectedStatus) throws Exception {
        resultActions.andExpect(status().is(expectedStatus));
    }

    @And("the trainer registration response should contain username and password")
    public void theTrainerRegistrationResponseShouldContainUsernameAndPassword() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertNotNull(response.username());
        assertNotNull(response.password());
    }
}