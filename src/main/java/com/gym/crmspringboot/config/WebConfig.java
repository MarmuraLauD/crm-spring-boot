package com.gym.crmspringboot.config;

import com.gym.crmspringboot.interceptor.TransactionLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TransactionLoggingInterceptor transactionLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(transactionLoggingInterceptor)
                .addPathPatterns("/api/v1/**");
    }

}
