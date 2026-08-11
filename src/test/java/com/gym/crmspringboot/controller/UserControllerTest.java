package com.gym.crmspringboot.controller;

import com.gym.crmspringboot.facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GymFacade gymFacade;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void changePassword_ReturnsStatusOk() throws Exception {
        // Arrange
        String username = "user";
        String oldPass = "old";
        String newPass = "new";

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/users/password")
                        .param("username", username)
                        .param("oldPassword", oldPass)
                        .param("newPassword", newPass))
                .andExpect(status().isOk());

        verify(gymFacade).changeUserPassword(username, oldPass, newPass);
    }

    @Test
    void toggleActive_ReturnsStatusOk() throws Exception {
        // Arrange
        String username = "user";
        Boolean status = true;

        // Act
        // Assert
        mockMvc.perform(patch("/api/v1/users/{username}/status", username)
                        .param("status", status.toString()))
                .andExpect(status().isOk());

        verify(gymFacade).toggleUserActive(username, status);
    }
}