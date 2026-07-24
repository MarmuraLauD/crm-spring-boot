package com.gym.crmspringboot.dto.response;

import lombok.Builder;

@Builder
public record TraineeItemResponse(

        String username,
        String firstName,
        String lastName

) { }
