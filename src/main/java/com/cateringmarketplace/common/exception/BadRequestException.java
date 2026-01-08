package com.cateringmarketplace.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for bad request errors.
 */
public class BadRequestException extends BaseException {

    private static final String ERROR_CODE = "BAD_REQUEST";

    public BadRequestException(String message) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, String details) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST, details);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST, cause);
    }
}

