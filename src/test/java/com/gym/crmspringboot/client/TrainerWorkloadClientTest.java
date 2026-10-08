package com.gym.crmspringboot.client;

import com.gym.crmspringboot.dto.response.TrainerWorkloadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestClient;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadClientTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    @Mock
    private DiscoveryClient discoveryClient;

    private TrainerWorkloadClient client;

    private JmsTemplate jmsTemplate;

    @BeforeEach
    void setUp() {
        when(restClientBuilder.requestInterceptor(any())).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);

        client = new TrainerWorkloadClient(restClientBuilder, discoveryClient, jmsTemplate);
    }

    @Test
    void getTrainerWorkloadFallback_ReturnsEmptySummary() {
        // Arrange
        String username = "Trainer.One";
        Throwable exception = new RuntimeException("Test Exception");

        // Act
        TrainerWorkloadResponse response = client.getTrainerWorkloadFallback(username, "Bearer token", exception);

        // Assert
        assertNotNull(response);
        assertEquals(username, response.getTrainerUsername());
        assertEquals(0, response.getYears().size());
    }

}