package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.service.LoginAttemptService;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final int MAX_ATTEMPT = 3;
    private static final long BLOCK_DURATION_MINUTES = 5;

    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> blockCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockCache.remove(username);
    }

    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);
        if (attempts >= MAX_ATTEMPT) {
            blockCache.put(username, Instant.now().plusSeconds(BLOCK_DURATION_MINUTES * 60));
        }
    }

    public boolean isBlocked(String username) {
        Instant blockTime = blockCache.get(username);
        if (blockTime == null) {
            return false;
        }
        if (Instant.now().isAfter(blockTime)) {
            blockCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }
        return true;
    }
}