package com.dcuobot.api.common.exception;

import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.census.exception.MissingDataException;
import com.dcuobot.api.character.exception.CharacterNotFoundException;
import com.dcuobot.api.common.sort.InvalidSortCriteriaException;
import com.dcuobot.api.common.worldid.InvalidWorldIdException;
import com.dcuobot.api.guild.exception.GuildNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translates the application's custom exceptions into the classic Spring Boot error response
 * shape ({@code timestamp}/{@code status}/{@code error}/{@code message}/{@code path}), the same
 * one produced by the default {@code /error} whitelabel handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidWorldIdException.class)
    public ResponseEntity<Object> handleInvalidWorldId(InvalidWorldIdException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(InvalidSortCriteriaException.class)
    public ResponseEntity<Object> handleInvalidSortCriteria(InvalidSortCriteriaException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(CharacterNotFoundException.class)
    public ResponseEntity<Object> handleCharacterNotFound(CharacterNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(GuildNotFoundException.class)
    public ResponseEntity<Object> handleGuildNotFound(GuildNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(MissingDataException.class)
    public ResponseEntity<Object> handleMissingData(MissingDataException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(CensusException.class)
    public ResponseEntity<Object> handleCensus(CensusException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_GATEWAY, ex, request);
    }

    private static ResponseEntity<Object> buildResponse(HttpStatus status, Exception ex, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(status).body(body);
    }
}
