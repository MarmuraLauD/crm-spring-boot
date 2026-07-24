package com.gym.crmspringboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChangePasswordRequest(

        @NotBlank String username,
        @NotBlank String oldPassword,
        @NotBlank String newPassword

) { }
