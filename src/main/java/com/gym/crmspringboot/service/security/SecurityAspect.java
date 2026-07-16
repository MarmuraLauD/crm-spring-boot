package com.gym.crmspringboot.service.security;

import com.gym.crmspringboot.service.helper.CredentialsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityAspect {

    private final CredentialsService credentialsService;

    @Before(value = "@annotation(RequireAuth) && args(username, password, ..)",
            argNames = "pjp,username,password")
    public void authenticate(String username, String password) {
        log.info("AOP Security: checking credentials for user {}", username);

        if (!credentialsService.authenticate(username, password)) {
            log.warn("AOP Security: authentication failed for user {}", username);
            throw new IllegalArgumentException("Authentication failed: invalid username or password");
        }
    }
}
