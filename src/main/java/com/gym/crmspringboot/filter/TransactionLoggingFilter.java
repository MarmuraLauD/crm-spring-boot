package com.gym.crmspringboot.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private static final String TRANSACTION_ID = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String transactionId = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, transactionId);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            log.info("Incoming REST Call: METHOD=[{}], URI=[{}]", request.getMethod(), request.getRequestURI());

            filterChain.doFilter(request, responseWrapper);

        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();

            String responseBody = new String(responseWrapper.getContentAsByteArray());

            log.info("Outgoing REST Response: METHOD=[{}], URI=[{}], STATUS=[{}], DURATION=[{}ms], BODY=[{}]",
                    request.getMethod(), request.getRequestURI(), status, duration, responseBody);

            responseWrapper.copyBodyToResponse();

            MDC.clear();
        }
    }
}