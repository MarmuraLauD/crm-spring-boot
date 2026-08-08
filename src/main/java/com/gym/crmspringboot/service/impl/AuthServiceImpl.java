package com.gym.crmspringboot.service.impl;

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
import com.gym.crmspringboot.service.AuthService;
import com.gym.crmspringboot.service.LoginAttemptService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public JwtTokensDto login(AuthRequest authRequest) {

        if (loginAttemptService.isBlocked(authRequest.username())) {
            throw new LockedException("User is blocked due to multiple failed login attempts. Try again in 5 minutes.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
            );
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(authRequest.username());
            throw e;
        }

        loginAttemptService.loginSucceeded(authRequest.username());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username());
        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshTokenString = jwtUtils.generateRefreshToken(userDetails);

        User user = userRepository.findByUsername(authRequest.username())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenString)
                .expiryDate(jwtUtils.extractExpiration(refreshTokenString).toInstant())
                .build();

        refreshTokenRepository.save(refreshToken);

        return new JwtTokensDto(accessToken, refreshTokenString);
    }

    @Override
    @Transactional
    public JwtTokensDto refreshToken(String requestRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found in database"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token has expired");
        }

        String username = refreshToken.getUser().getUsername();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (Boolean.FALSE.equals(jwtUtils.validateToken(requestRefreshToken, userDetails))) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String newAccessToken = jwtUtils.generateAccessToken(userDetails);

        return new JwtTokensDto(newAccessToken, requestRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

}
