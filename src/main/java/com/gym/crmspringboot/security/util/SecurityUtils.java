package com.gym.crmspringboot.security.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class SecurityUtils {

    private static final String BEARER_PREFIX = "Bearer ";

    private SecurityUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String extractAuthToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            String bearerToken = attributes.getRequest().getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
                return bearerToken;
            }
        }

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String credentials) {
            return credentials.startsWith(BEARER_PREFIX) ? credentials : BEARER_PREFIX + credentials;
        }

        return "";
    }

}