package com.gym.crmspringboot.controller;

import com.gym.crmspringboot.controller.api.AuthApi;
import com.gym.crmspringboot.dto.request.AuthRequest;
import com.gym.crmspringboot.dto.response.AuthResponse;
import com.gym.crmspringboot.dto.response.JwtTokensDto;
import com.gym.crmspringboot.security.util.JwtUtils;
import com.gym.crmspringboot.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @Override
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        JwtTokensDto tokens = authService.login(authRequest);

        ResponseCookie cookie = jwtUtils.generateRefreshJwtCookie(tokens.refreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return AuthResponse.builder()
                .token(tokens.accessToken())
                .build();
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@CookieValue(name = "refresh_token") String refreshToken) {
        JwtTokensDto tokens = authService.refreshToken(refreshToken);

        return AuthResponse.builder()
                .token(tokens.accessToken())
                .build();
    }

    @PostMapping("/logout")
    public void logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            authService.logout(refreshToken);
        }

        ResponseCookie cookie = jwtUtils.getCleanJwtRefreshCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
