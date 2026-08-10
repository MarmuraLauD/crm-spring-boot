package com.gym.crmspringboot.monitoring;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("databaseLatencyHealth")
public class DatabaseLatencyHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;
    private static final long MAX_LATENCY_MS = 500;

    public DatabaseLatencyHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        long start = System.currentTimeMillis();
        try {
            jdbcTemplate.execute("SELECT 1");
            long latency = System.currentTimeMillis() - start;

            if (latency < MAX_LATENCY_MS) {
                return Health.up()
                        .withDetail("latency_ms", latency)
                        .withDetail("status", "Fast")
                        .build();
            } else {
                return Health.down()
                        .withDetail("latency_ms", latency)
                        .withDetail("error", "Database is responding too slowly")
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e).withDetail("error", "Database connection failed").build();
        }
    }
}