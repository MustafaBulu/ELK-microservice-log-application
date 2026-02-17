package com.mustafabulu.elk.userservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
@Schema(description = "Standard API error payload")
public class ApiErrorResponse {
    @Schema(description = "Response timestamp in UTC", example = "2026-02-17T01:00:00Z")
    Instant timestamp;
    @Schema(description = "HTTP status code", example = "400")
    int status;
    @Schema(description = "HTTP status reason", example = "Bad Request")
    String error;
    @Schema(description = "Detailed error message", example = "Invalid request")
    String message;
    @Schema(description = "Request path", example = "/user/get-user")
    String path;
}

