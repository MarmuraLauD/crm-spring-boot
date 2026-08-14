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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LiquibaseReadinessHealthIndicatorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private LiquibaseReadinessHealthIndicator healthIndicator;

    @Test
    void health_ReturnsUp_WhenMigrationsExist() {
        // Arrange
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(5);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assert health != null;
        assertEquals(Status.UP, health.getStatus());
        assertEquals(5, health.getDetails().get("migrations_applied"));
    }

    @Test
    void health_ReturnsDown_WhenNoMigrationsFound() {
        // Arrange
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(0);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assert health != null;
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("No migrations found, DB not ready", health.getDetails().get("error"));
    }

    @Test
    void health_ReturnsDown_WhenQueryThrowsException() {
        // Arrange
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenThrow(new RuntimeException("DB offline"));

        // Act
        Health health = healthIndicator.health();

        // Assert
        assert health != null;
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Cannot query databasechangelog", health.getDetails().get("error"));
    }
}