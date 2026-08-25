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

@Component
@Slf4j
public class TransactionFilter extends OncePerRequestFilter {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String TRANSACTION_ID_KEY = "transactionId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    )
            throws ServletException, IOException {

        String transactionId = request.getHeader(TRANSACTION_ID_HEADER);

        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID_KEY, transactionId);

        log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info("Outgoing response: status {}", response.getStatus());
            MDC.remove(TRANSACTION_ID_KEY);
        }
    }
}