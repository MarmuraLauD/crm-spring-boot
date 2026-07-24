package com.gym.crmspringboot.dto.response;

import lombok.Builder;

@Builder
public record TrainingTypeItemResponse(

        Long trainingTypeId,
        String trainingType

) { }
