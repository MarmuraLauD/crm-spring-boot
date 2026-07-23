package com.gym.crmspringboot.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateTrainerRequest(

        @NotBlank String username,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String specialization,
        @NotNull Boolean isActive

) { }
