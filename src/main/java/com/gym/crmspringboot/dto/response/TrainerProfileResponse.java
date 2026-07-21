package com.gym.crmspringboot.dto.response;

import java.util.List;

public record TrainerProfileResponse(

        String firstName,
        String lastName,
        String specialization,
        Boolean isActive,
        List<TraineeItemResponse> trainees

) { }
