package com.taller.gestion_taller.exception;

import com.taller.gestion_taller.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateResource(
            DuplicateResourceException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request, null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(
            BadRequestException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage()));

        return buildResponse(HttpStatus.BAD_REQUEST, "La solicitud contiene datos invalidos", request, validationErrors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "La operacion viola una restriccion de datos", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request, null);
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                validationErrors);

        return ResponseEntity.status(status).body(response);
    }
}
