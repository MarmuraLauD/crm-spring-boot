package com.gym.crmspringboot.dto.response;

import lombok.Builder;

@Builder
public record RegistrationResponse(

        String username,
        String password

) { }
