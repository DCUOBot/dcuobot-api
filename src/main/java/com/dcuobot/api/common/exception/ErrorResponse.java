package com.dcuobot.api.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

/**
 * Documents the error body shape produced by {@link GlobalExceptionHandler}. Not constructed or
 * returned directly; referenced only from {@code @ApiResponse} schemas.
 */
@Data
@Schema(description = "Error response returned for a failed request.")
public class ErrorResponse {
    @Schema(description = "Time the error occurred.", example = "2026-09-01T12:34:56.789Z")
    private Instant timestamp;

    @Schema(description = "HTTP status code.", example = "404")
    private int status;

    @Schema(description = "HTTP status reason phrase.", example = "Not Found")
    private String error;

    @Schema(description = "Human-readable error message.", example = "Character not found.")
    private String message;

    @Schema(description = "Request path that produced the error.", example = "/v1/census/characters")
    private String path;
}
