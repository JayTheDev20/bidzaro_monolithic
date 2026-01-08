package com.cateringmarketplace.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Standard API response wrapper for all endpoints.
 *
 * @param <T> The type of data contained in the response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates whether the request was successful
     */
    private boolean success;

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Human-readable message
     */
    private String message;

    /**
     * Response data payload
     */
    private T data;

    /**
     * Error details (only present when success is false)
     */
    private ErrorResponse error;

    /**
     * Response timestamp
     */
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * Pagination information (optional)
     */
    private PageInfo pageInfo;

    /**
     * Creates a successful response with data.
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message("Success")
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates a successful response with data and custom message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates a successful response with data and pagination info.
     */
    public static <T> ApiResponse<T> success(T data, PageInfo pageInfo) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message("Success")
                .data(data)
                .pageInfo(pageInfo)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates a successful response with data, message, and pagination info.
     */
    public static <T> ApiResponse<T> success(T data, String message, PageInfo pageInfo) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .pageInfo(pageInfo)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates a successful created response (201).
     */
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(201)
                .message("Created successfully")
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates a successful created response with custom message.
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(201)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates an error response.
     */
    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(error.getStatus() != null ? error.getStatus() : 500)
                .message(error.getMessage())
                .error(error)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates an error response with status code.
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status)
                .message(message)
                .timestamp(Instant.now())
                .build();

        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .error(errorResponse)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates a no content response (204).
     */
    public static <T> ApiResponse<T> noContent() {
        return ApiResponse.<T>builder()
                .success(true)
                .status(204)
                .message("No content")
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates an accepted response (202).
     */
    public static <T> ApiResponse<T> accepted(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(202)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }
}

