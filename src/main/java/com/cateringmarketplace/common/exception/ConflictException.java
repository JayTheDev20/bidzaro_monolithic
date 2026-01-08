package com.cateringmarketplace.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is a resource conflict.
 */
public class ConflictException extends BaseException {

    private static final String ERROR_CODE = "CONFLICT";

    public ConflictException(String message) {
        super(message, ERROR_CODE, HttpStatus.CONFLICT);
    }

    public ConflictException(String message, String details) {
        super(message, ERROR_CODE, HttpStatus.CONFLICT, details);
    }
}

