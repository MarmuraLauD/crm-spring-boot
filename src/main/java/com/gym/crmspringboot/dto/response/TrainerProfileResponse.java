package com.gym.crmspringboot.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record TrainerProfileResponse(

        String firstName,
        String lastName,
        String specialization,
        Boolean isActive,
        List<TraineeItemResponse> trainees

) { }
