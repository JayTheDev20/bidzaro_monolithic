package com.cateringmarketplace.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user does not have permission to access a resource.
 */
public class ForbiddenException extends BaseException {

    private static final String ERROR_CODE = "FORBIDDEN";

    public ForbiddenException(String message) {
        super(message, ERROR_CODE, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String message, String details) {
        super(message, ERROR_CODE, HttpStatus.FORBIDDEN, details);
    }
}

