package com.cateringmarketplace.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when rate limit is exceeded.
 */
public class RateLimitExceededException extends BaseException {

    private static final String ERROR_CODE = "RATE_LIMIT_EXCEEDED";
    private final long retryAfterSeconds;

    public RateLimitExceededException(String message) {
        super(message, ERROR_CODE, HttpStatus.TOO_MANY_REQUESTS);
        this.retryAfterSeconds = 60;
    }

    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(message, ERROR_CODE, HttpStatus.TOO_MANY_REQUESTS);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}

