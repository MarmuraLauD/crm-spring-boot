package com.gym.crmspringboot.monitoring;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("liquibaseReadinessHealth")
public class LiquibaseReadinessHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public LiquibaseReadinessHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM databasechangelog", Integer.class);

            if (count != null && count > 0) {
                return Health.up().withDetail("migrations_applied", count).build();
            } else {
                return Health.down().withDetail("error", "No migrations found, DB not ready").build();
            }
        } catch (Exception e) {
            return Health.down(e).withDetail("error", "Cannot query databasechangelog").build();
        }
    }
}
