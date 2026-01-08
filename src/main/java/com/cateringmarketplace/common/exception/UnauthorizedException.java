package com.cateringmarketplace.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user is not authorized to access a resource.
 */
public class UnauthorizedException extends BaseException {

    private static final String ERROR_CODE = "UNAUTHORIZED";

    public UnauthorizedException(String message) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, String details) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED, details);
    }
}

