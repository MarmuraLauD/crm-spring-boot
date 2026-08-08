package com.gym.crmspringboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthRequest(
        @NotBlank String username,
        @NotBlank String password
) {

}
