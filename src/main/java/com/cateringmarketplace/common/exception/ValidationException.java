package com.cateringmarketplace.common.exception;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends BaseException {

    private static final String ERROR_CODE = "VALIDATION_ERROR";
    private final List<FieldError> fieldErrors;

    public ValidationException(String message) {
        super(message, ERROR_CODE, HttpStatus.UNPROCESSABLE_ENTITY);
        this.fieldErrors = new ArrayList<>();
    }

    public ValidationException(String message, List<FieldError> fieldErrors) {
        super(message, ERROR_CODE, HttpStatus.UNPROCESSABLE_ENTITY);
        this.fieldErrors = fieldErrors != null ? fieldErrors : new ArrayList<>();
    }

    public ValidationException(String field, String message) {
        super(message, ERROR_CODE, HttpStatus.UNPROCESSABLE_ENTITY);
        this.fieldErrors = new ArrayList<>();
        this.fieldErrors.add(new FieldError(field, message));
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldError(String field, String message) {
        this.fieldErrors.add(new FieldError(field, message));
    }

    public record FieldError(String field, String message) {}
}

