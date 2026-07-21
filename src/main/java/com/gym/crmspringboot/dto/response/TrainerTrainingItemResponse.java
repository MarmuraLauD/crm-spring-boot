package com.gym.crmspringboot.dto.response;

import java.time.LocalDate;

public record TrainerTrainingItemResponse(

        String trainingName,
        LocalDate trainingDate,
        String trainingType,
        Double trainingDuration,
        String traineeName

) { }
