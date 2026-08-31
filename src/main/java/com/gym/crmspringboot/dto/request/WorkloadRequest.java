package com.gym.crmspringboot.dto.request;

import com.gym.crmspringboot.dto.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadRequest {

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;
    private LocalDate trainingDate;
    private int trainingDuration;
    private ActionType actionType;

}