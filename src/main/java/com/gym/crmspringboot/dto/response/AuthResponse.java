package com.gym.crmspringboot.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(String token) {

}
