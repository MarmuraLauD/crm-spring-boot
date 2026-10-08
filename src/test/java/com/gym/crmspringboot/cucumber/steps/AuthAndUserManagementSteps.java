package com.gym.crmspringboot.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crmspringboot.dto.request.AuthRequest;
import com.gym.crmspringboot.dto.response.AuthResponse;
import com.gym.crmspringboot.model.Role;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.model.TrainingType;
import com.gym.crmspringboot.repository.TrainerRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthAndUserManagementSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AuthRequest authRequest;
    private Cookie refreshCookie;
    private ResultActions resultActions;
    private String accessToken;

    private void ensureUserExists(String username, String password) {
        if (trainerRepository.findByUsername(username).isEmpty()) {
            TrainingType spec = new TrainingType();
            spec.setId(1L);

            Trainer trainer = Trainer.builder()
                    .username(username)
                    .firstName("TestFirst")
                    .lastName("TestLast")
                    .password(passwordEncoder.encode(password))
                    .active(true)
                    .role(Role.ROLE_TRAINER)
                    .specialization(spec)
                    .build();
            trainerRepository.save(trainer);
        }
    }

    @Given("a valid auth request for username {string} and password {string}")
    public void aValidAuthRequestForUsernameAndPassword(String username, String password) {
        ensureUserExists(username, password);
        authRequest = AuthRequest.builder()
                .username(username)
                .password(password)
                .build();
    }

    @Given("an invalid auth request for username {string} and password {string}")
    public void anInvalidAuthRequestForUsernameAndPassword(String username, String password) {
        ensureUserExists(username, "correct_password_123");
        authRequest = AuthRequest.builder()
                .username(username)
                .password(password)
                .build();
    }

    @Given("a valid refresh token cookie")
    public void aValidRefreshTokenCookie() throws Exception {
        String uniqueUser = "RefreshUser." + java.util.UUID.randomUUID().toString().substring(0, 8);
        ensureUserExists(uniqueUser, "password123");

        AuthRequest req = AuthRequest.builder()
                .username(uniqueUser)
                .password("password123")
                .build();

        ResultActions loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        String setCookieHeader = loginResult.andReturn().getResponse().getHeader("Set-Cookie");
        String responseBody = loginResult.andReturn().getResponse().getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        accessToken = authResponse.token();

        String tokenValue = setCookieHeader.split("refresh_token=")[1].split(";")[0];
        refreshCookie = new jakarta.servlet.http.Cookie("refresh_token", tokenValue);
    }

    @When("a POST request is made to login at {string}")
    public void aPostRequestIsMadeToLoginAt(String endpoint) throws Exception {
        resultActions = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)));
    }

    @When("a POST request is made to refresh at {string}")
    public void aPostRequestIsMadeToRefreshAt(String endpoint) throws Exception {
        resultActions = mockMvc.perform(post(endpoint)
                .header("Authorization", "Bearer " + accessToken)
                .cookie(refreshCookie));
    }

    @When("a POST request is made to logout at {string}")
    public void aPostRequestIsMadeToLogoutAt(String endpoint) throws Exception {
        resultActions = mockMvc.perform(post(endpoint)
                .header("Authorization", "Bearer " + accessToken)
                .cookie(refreshCookie));
    }

    @When("a PUT request is made to change password at {string} with username {string}, old password {string}, and new password {string}")
    public void aPutRequestIsMadeToChangePasswordAtWithUsernameOldPasswordAndNewPassword(String endpoint, String username, String oldPassword, String newPassword) throws Exception {
        ensureUserExists(username, oldPassword);
        resultActions = mockMvc.perform(put(endpoint)
                .with(user(username).roles("TRAINER"))
                .param("username", username)
                .param("oldPassword", oldPassword)
                .param("newPassword", newPassword));
    }

    @When("a PATCH request is made by trainer to toggle status at {string} to {word}")
    public void aPatchRequestIsMadeByTrainerToToggleStatusAtTo(String endpoint, String status) throws Exception {
        ensureUserExists("John.Doe", "dummy_password");
        resultActions = mockMvc.perform(patch(endpoint)
                .with(user("trainer_user").roles("TRAINER"))
                .param("status", status));
    }

    @Then("the auth response status should be {int}")
    public void theAuthResponseStatusShouldBe(int expectedStatus) throws Exception {
        resultActions.andExpect(status().is(expectedStatus));
    }

    @Then("the user response status should be {int}")
    public void theUserResponseStatusShouldBe(int expectedStatus) throws Exception {
        resultActions.andExpect(status().is(expectedStatus));
    }

    @And("the response should contain an access token and a refresh cookie")
    public void theResponseShouldContainAnAccessTokenAndARefreshCookie() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        AuthResponse response = objectMapper.readValue(responseBody, AuthResponse.class);

        assertNotNull(response.token());
        resultActions.andExpect(cookie().exists("refresh_token"));
    }

    @And("the response should contain a new access token")
    public void theResponseShouldContainANewAccessToken() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        AuthResponse response = objectMapper.readValue(responseBody, AuthResponse.class);

        assertNotNull(response.token());
    }
}