package com.example.FMIbook.utils.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.Data;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Data
public class BaseExceptionHandler {
    private final Logger logger;

    public BaseExceptionHandler(Logger logger) {
        this.logger = logger;
    }

    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 404);
        errorResponse.put("code", ex.getCode());
        errorResponse.put("message", ex.getMessage());
        logger.error(errorResponse.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Map<String, Object>> handleDomainException(DomainException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 400);
        errorResponse.put("code", ex.getCode());
        errorResponse.put("message", ex.getMessage());
        logger.error(errorResponse.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Map<String, Object>> handleForbiddenException(ForbiddenException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 403);
        errorResponse.put("code", ex.getCode());
        errorResponse.put("message", ex.getMessage());
        logger.error(errorResponse.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<Map<String, Object>> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        errorResponse.put("errors", errors);
        errorResponse.put("status", 400);
        errorResponse.put("message", "validation errors");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<Map<String, Object>> handleConstraintException(ConstraintViolationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, Object> errors = new HashMap<>();
        ex.getConstraintViolations().forEach((error) -> {
            Path fieldName = error.getPropertyPath();
            String errorMessage = error.getMessageTemplate();
            errors.put(fieldName.toString(), errorMessage);
        });
        errorResponse.put("errors", errors);
        errorResponse.put("status", 400);
        errorResponse.put("message", "validation errors");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
