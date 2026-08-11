package com.gym.crmspringboot.monitoring;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DatabaseLatencyHealthIndicatorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DatabaseLatencyHealthIndicator healthIndicator;

    @Test
    void health_ReturnsUp_WhenDatabaseIsFast() {
        // Arrange
        // Act
        Health health = healthIndicator.health();

        // Assert
        assert health != null;
        assertEquals(Status.UP, health.getStatus());
        assertEquals("Fast", health.getDetails().get("status"));
        verify(jdbcTemplate, times(1)).execute("SELECT 1");
    }

    @Test
    void health_ReturnsDown_WhenDatabaseThrowsException() {
        // Arrange
        doThrow(new RuntimeException("Connection error")).when(jdbcTemplate).execute("SELECT 1");

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Database connection failed", health.getDetails().get("error"));
    }
}