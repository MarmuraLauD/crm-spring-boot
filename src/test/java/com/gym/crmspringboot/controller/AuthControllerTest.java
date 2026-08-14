package com.gym.crmspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crmspringboot.dto.request.AuthRequest;
import com.gym.crmspringboot.dto.response.JwtTokensDto;
import com.gym.crmspringboot.security.util.JwtUtils;
import com.gym.crmspringboot.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_ReturnsTokenAndSetsCookie() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest("user", "password");
        JwtTokensDto tokens = new JwtTokensDto("access_token", "refresh_token");
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "refresh_token")
                .httpOnly(true).path("/api/v1/auth/refresh").build();

        when(authService.login(any(AuthRequest.class))).thenReturn(tokens);
        when(jwtUtils.generateRefreshJwtCookie("refresh_token")).thenReturn(cookie);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access_token"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, cookie.toString()));
    }

    @Test
    void refresh_ReturnsNewToken() throws Exception {
        // Arrange
        String oldRefreshToken = "old_refresh_token";
        JwtTokensDto tokens = new JwtTokensDto("new_access_token", "new_refresh_token");
        Cookie requestCookie = new Cookie("refresh_token", oldRefreshToken);

        when(authService.refreshToken(oldRefreshToken)).thenReturn(tokens);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(requestCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new_access_token"));
    }

    @Test
    void refresh_WithoutCookie_ReturnsBadRequest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_WithCookie_CallsServiceAndClearsCookie() throws Exception {
        // Arrange
        String refreshToken = "token_to_delete";
        Cookie requestCookie = new Cookie("refresh_token", refreshToken);
        ResponseCookie cleanCookie = ResponseCookie.from("refresh_token", "").maxAge(0).build();

        when(jwtUtils.getCleanJwtRefreshCookie()).thenReturn(cleanCookie);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(requestCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().value("refresh_token", ""))
                .andExpect(cookie().maxAge("refresh_token", 0));

        verify(authService).logout(refreshToken);
    }

    @Test
    void logout_WithoutCookie_DoesNotCallServiceButClearsCookie() throws Exception {
        // Arrange
        ResponseCookie cleanCookie = ResponseCookie.from("refresh_token", "").maxAge(0).build();
        when(jwtUtils.getCleanJwtRefreshCookie()).thenReturn(cleanCookie);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("refresh_token", ""))
                .andExpect(cookie().maxAge("refresh_token", 0));

        verifyNoInteractions(authService);
    }

}