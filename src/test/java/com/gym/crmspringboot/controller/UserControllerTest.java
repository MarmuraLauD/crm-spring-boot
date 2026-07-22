package com.gym.crmspringboot.controller;

import com.gym.crmspringboot.facade.GymFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymFacade gymFacade;

    @Test
    void login_ShouldReturnOkStatus() throws Exception {
        // Arrange
        String username = "John.Doe";
        String password = "password123";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verifyNoInteractions(gymFacade);
    }

    @Test
    void changePassword_ShouldReturnOkStatus() throws Exception {
        // Arrange
        String username = "John.Doe";
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/password")
                        .param("username", username)
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(gymFacade).changeUserPassword(username, oldPassword, newPassword);
    }

    @Test
    void toggleActive_ShouldReturnOkStatus() throws Exception {
        // Arrange
        String username = "John.Doe";
        String password = "password123";
        Boolean status = true;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/{username}/status", username)
                        .param("password", password)
                        .param("status", status.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(gymFacade).toggleUserActive(username, password, status);
    }
}