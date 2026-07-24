package com.gym.crmspringboot.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
    public ErrorResponse(int status, String message) {
        this(status, message, LocalDateTime.now((ZoneId.systemDefault())), null);
    }

    public ErrorResponse(int status, String message, Map<String, String> errors) {
        this(status, message, LocalDateTime.now((ZoneId.systemDefault())), errors);
    }
}