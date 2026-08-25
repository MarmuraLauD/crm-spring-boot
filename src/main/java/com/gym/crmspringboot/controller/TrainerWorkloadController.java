package com.gym.crmspringboot.controller;

import com.gym.crmspringboot.client.TrainerWorkloadClient;
import com.gym.crmspringboot.controller.api.TrainerWorkloadControllerAPI;
import com.gym.crmspringboot.dto.response.TrainerWorkloadResponse;
import com.gym.crmspringboot.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/workloads")
@RequiredArgsConstructor
public class TrainerWorkloadController implements TrainerWorkloadControllerAPI {

    private final TrainerWorkloadClient workloadClient;

    @GetMapping("/{username}")
    public TrainerWorkloadResponse getTrainerWorkload(@PathVariable String username) {
        log.info("Received request to fetch workload for trainer: {}", username);

        String token = SecurityUtils.extractAuthToken();
        return workloadClient.getTrainerWorkload(username, token);
    }

}