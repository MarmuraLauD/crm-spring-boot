package com.gym.crmspringboot.dto.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TrainerTrainingItemResponse(

        String trainingName,
        LocalDate trainingDate,
        String trainingType,
        Double trainingDuration,
        String traineeName

) { }
