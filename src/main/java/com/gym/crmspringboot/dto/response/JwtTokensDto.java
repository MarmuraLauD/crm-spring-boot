package com.gym.crmspringboot.dto.response;

public record JwtTokensDto(
        String accessToken,
        String refreshToken
) {

}
