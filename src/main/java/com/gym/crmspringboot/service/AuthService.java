package com.gym.crmspringboot.service;

import com.gym.crmspringboot.dto.request.AuthRequest;
import com.gym.crmspringboot.dto.response.JwtTokensDto;

public interface AuthService {

    JwtTokensDto login(AuthRequest authRequest);

    JwtTokensDto refreshToken(String requestRefreshToken);

    void logout(String token);

}
