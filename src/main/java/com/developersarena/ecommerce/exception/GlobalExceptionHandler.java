package com.developersarena.ecommerce.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionDTO> handleBusinessException(
            BusinessException ex
    ) {
        log.error("Business exception occurred", ex);
        ExceptionDTO dto = new ExceptionDTO(
                ex.getErrorCode(),
                ex.getMessage(),
                String.valueOf(new Date())
        );

        return new ResponseEntity<>(dto,ex.getStatus());
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ExceptionDTO> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ExceptionDTO dto = new ExceptionDTO(
                "ACCESS_DENIED",
                "You do not have permission to access this resource",
                String.valueOf(new Date())
        );
        return new ResponseEntity<>(dto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ExceptionDTO> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ExceptionDTO dto = new ExceptionDTO(
                "UNAUTHORIZED",
                "Please login to access this resource",
                String.valueOf(new Date())
        );
        return new ResponseEntity<>(dto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ExceptionDTO> handleConstraintViolation(
            jakarta.validation.ConstraintViolationException ex) {

        // Extract first validation message
        String message = ex.getConstraintViolations()
                .iterator()
                .next()
                .getMessage();

        log.warn("Validation failed: {}", message);

        ExceptionDTO dto = new ExceptionDTO(
                "VALIDATION_ERROR",
                message,
                String.valueOf(new Date())
        );

        return ResponseEntity.badRequest().body(dto);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionDTO> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not supported: {}", ex.getMessage());
        ExceptionDTO dto = new ExceptionDTO(
                "METHOD_NOT_SUPPORTED",
                "Method not supported",
                String.valueOf(new Date())
        );
        return ResponseEntity.badRequest().body(dto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        ExceptionDTO dto = new ExceptionDTO(
                "VALIDATION_ERROR",
                message,
                String.valueOf(new Date())
        );

        return ResponseEntity.badRequest().body(dto);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDTO> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred", ex);
        ExceptionDTO dto = new ExceptionDTO(
                "BAD_REQUEST",
                ex.getMessage(),
                String.valueOf(new Date())
        );
        return ResponseEntity.badRequest().body(dto);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleGenericException(Exception ex) {

        log.error("Unexpected exception", ex);

        ExceptionDTO dto = new ExceptionDTO(
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                String.valueOf(new Date())
        );

        return ResponseEntity.internalServerError().body(dto);
    }
}
