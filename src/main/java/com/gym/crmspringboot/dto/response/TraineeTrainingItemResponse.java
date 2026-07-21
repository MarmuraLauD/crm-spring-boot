package com.gym.crmspringboot.dto.response;

import java.time.LocalDate;

public record TraineeTrainingItemResponse(

        String trainingName,
        LocalDate trainingDate,
        String trainingType,
        Double trainingDuration,
        String trainerName

) { }
