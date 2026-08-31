package com.gym.crmspringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearSummaryResponse {
    private int yearValue;
    private List<MonthSummaryResponse> months;
}