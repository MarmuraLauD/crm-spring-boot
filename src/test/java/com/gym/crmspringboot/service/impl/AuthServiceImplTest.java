package com.gym.crmspringboot.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gym.crmspringboot.dto.request.AuthRequest;
import com.gym.crmspringboot.dto.response.JwtTokensDto;
import com.gym.crmspringboot.exception.InvalidTokenException;
import com.gym.crmspringboot.exception.LockedException;
import com.gym.crmspringboot.exception.UserNotFoundException;
import com.gym.crmspringboot.model.RefreshToken;
import com.gym.crmspringboot.model.User;
import com.gym.crmspringboot.repository.RefreshTokenRepository;
import com.gym.crmspringboot.repository.UserRepository;
import com.gym.crmspringboot.security.util.JwtUtils;
import com.gym.crmspringboot.service.LoginAttemptService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_ThrowsLockedException_WhenUserIsBlocked() {
        // Arrange
        AuthRequest request = new AuthRequest("user", "pass");
        when(loginAttemptService.isBlocked("user")).thenReturn(true);

        // Act
        // Assert
        assertThrows(LockedException.class, () -> authService.login(request));
    }

    @Test
    void login_ThrowsBadCredentialsException_AndRecordsFailedAttempt() {
        // Arrange
        AuthRequest request = new AuthRequest("user", "wrong_pass");
        when(loginAttemptService.isBlocked("user")).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        // Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(loginAttemptService).loginFailed("user");
    }

    @Test
    void login_ThrowsUserNotFoundException_WhenUserNotInDb() {
        // Arrange
        AuthRequest request = new AuthRequest("user", "pass");
        UserDetails userDetails = mock(UserDetails.class);
        when(loginAttemptService.isBlocked("user")).thenReturn(false);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtUtils.generateAccessToken(userDetails)).thenReturn("access_token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("refresh_token");
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void login_ReturnsTokens_OnSuccessfulAuthentication() {
        // Arrange
        AuthRequest request = new AuthRequest("user", "pass");
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setUsername("user");
        Date futureDate = Date.from(Instant.now().plus(7, ChronoUnit.DAYS));

        when(loginAttemptService.isBlocked("user")).thenReturn(false);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtUtils.generateAccessToken(userDetails)).thenReturn("access_token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("refresh_token");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtUtils.extractExpiration("refresh_token")).thenReturn(futureDate);

        // Act
        JwtTokensDto result = authService.login(request);

        // Assert
        assertEquals("access_token", result.accessToken());
        assertEquals("refresh_token", result.refreshToken());
        verify(loginAttemptService).loginSucceeded("user");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refreshToken_ThrowsInvalidTokenException_WhenTokenNotFoundInDb() {
        // Arrange
        String token = "some_token";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(InvalidTokenException.class, () -> authService.refreshToken(token));
    }

    @Test
    void refreshToken_ThrowsInvalidTokenException_WhenTokenExpired() {
        // Arrange
        String token = "expired_token";
        RefreshToken refreshToken = RefreshToken.builder()
                .expiryDate(Instant.now().minusSeconds(3600))
                .build();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        // Act
        // Assert
        assertThrows(InvalidTokenException.class, () -> authService.refreshToken(token));
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void refreshToken_ReturnsNewAccessToken_WhenTokenIsValid() {
        // Arrange
        String token = "valid_token";
        User user = new User();
        user.setUsername("user");
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();
        UserDetails userDetails = mock(UserDetails.class);

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtUtils.validateToken(token, userDetails)).thenReturn(true);
        when(jwtUtils.generateAccessToken(userDetails)).thenReturn("new_access_token");

        // Act
        JwtTokensDto result = authService.refreshToken(token);

        // Assert
        assertEquals("new_access_token", result.accessToken());
        assertEquals(token, result.refreshToken());
    }

    @Test
    void logout_DeletesToken() {
        // Arrange
        String token = "token_to_delete";

        // Act
        authService.logout(token);

        // Assert
        verify(refreshTokenRepository).deleteByToken(token);
    }

}
