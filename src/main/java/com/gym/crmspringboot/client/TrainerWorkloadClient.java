package com.gym.crmspringboot.client;

import com.gym.crmspringboot.dto.request.WorkloadRequest;
import com.gym.crmspringboot.dto.response.TrainerWorkloadResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.cloud.client.ServiceInstance;

@Component
@Slf4j
public class TrainerWorkloadClient {

    private final RestClient restClient;
    private final DiscoveryClient discoveryClient;

    public TrainerWorkloadClient(RestClient.Builder builder, DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.restClient = builder
                .requestInterceptor((request, body, execution) -> {
                    String transactionId = MDC.get("transactionId");
                    if (transactionId != null) {
                        request.getHeaders().add("X-Transaction-Id", transactionId);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    private String getServiceUri() {
        return Optional.of(discoveryClient.getInstances("TRAINER-WORKLOAD-SERVICE"))
                .stream()
                .flatMap(List::stream)
                .findFirst()
                .map(ServiceInstance::getUri)
                .map(URI::toString)
                .orElseThrow(() -> new IllegalStateException("No active instances found for TRAINER-WORKLOAD-SERVICE"));
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "updateWorkloadFallback")
    public void updateWorkload(WorkloadRequest workloadRequest, String token) {
        String baseUrl = getServiceUri();
        restClient.post()
                .uri(baseUrl + "/api/v1/workloads")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(workloadRequest)
                .retrieve()
                .toBodilessEntity();
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "getTrainerWorkloadFallback")
    public TrainerWorkloadResponse getTrainerWorkload(String username, String token) {
        String baseUrl = getServiceUri();
        return restClient.get()
                .uri(baseUrl + "/api/v1/workloads/{username}", username)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .body(TrainerWorkloadResponse.class);
    }

    public void updateWorkloadFallback(WorkloadRequest workloadRequest, String token, Throwable throwable) {
        log.error("Failed to update workload for trainer {}. Error: {}", workloadRequest.getTrainerUsername(), throwable.getMessage());
    }

    public TrainerWorkloadResponse getTrainerWorkloadFallback(String username, String token, Throwable throwable) {
        log.error("Failed to fetch workload for trainer {}. Returning empty summary. Error: {}", username, throwable.getMessage());

        TrainerWorkloadResponse fallbackResponse = new TrainerWorkloadResponse();
        fallbackResponse.setTrainerUsername(username);
        fallbackResponse.setYears(Collections.emptyList());

        return fallbackResponse;
    }
}