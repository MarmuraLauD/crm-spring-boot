package com.gym.crmspringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummaryResponse {
    private int monthValue;
    private int trainingSummaryDuration;
}