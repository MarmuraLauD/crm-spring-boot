package com.gym.crmspringboot.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TraineeRegistrationRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        LocalDate dateOfBirth,
        String address

) { }
