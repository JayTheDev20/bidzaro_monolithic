package com.cateringmarketplace.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when payment processing fails.
 */
public class PaymentException extends BaseException {

    private static final String ERROR_CODE = "PAYMENT_ERROR";
    private final String transactionId;

    public PaymentException(String message) {
        super(message, ERROR_CODE, HttpStatus.PAYMENT_REQUIRED);
        this.transactionId = null;
    }

    public PaymentException(String message, String transactionId) {
        super(message, ERROR_CODE, HttpStatus.PAYMENT_REQUIRED);
        this.transactionId = transactionId;
    }

    public PaymentException(String message, String transactionId, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.PAYMENT_REQUIRED, cause);
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}

