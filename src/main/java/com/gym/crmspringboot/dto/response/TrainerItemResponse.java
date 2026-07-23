package com.gym.crmspringboot.dto.response;

import lombok.Builder;

@Builder
public record TrainerItemResponse(

        String username,
        String firstName,
        String lastName,
        String specialization

) { }
