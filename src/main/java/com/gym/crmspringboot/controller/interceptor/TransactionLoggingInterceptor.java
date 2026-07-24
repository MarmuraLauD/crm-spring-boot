package com.gym.crmspringboot.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TransactionLoggingInterceptor implements HandlerInterceptor {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String START_TIME_ATTR = "startTime";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String transactionId = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, transactionId);

        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());

        log.info("Incoming REST Call: METHOD=[{}], URI=[{}]", request.getMethod(), request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        Object startTimeObj = request.getAttribute(START_TIME_ATTR);
        long duration = 0;

        if (startTimeObj != null) {
            duration = System.currentTimeMillis() - (long) startTimeObj;
        }

        int status = response.getStatus();

        log.info("Outgoing REST Response: METHOD=[{}], URI=[{}], STATUS=[{}], DURATION=[{}ms]",
                request.getMethod(), request.getRequestURI(), status, duration);

        MDC.clear();
    }
}