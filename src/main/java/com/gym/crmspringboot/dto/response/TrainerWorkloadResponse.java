package com.gym.crmspringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadResponse {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean trainerStatus;
    private List<YearSummaryResponse> years;
}