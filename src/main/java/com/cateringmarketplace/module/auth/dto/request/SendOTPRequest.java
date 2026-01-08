package com.cateringmarketplace.module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for sending OTP.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendOTPRequest {

    /**
     * Email or phone number to send OTP to
     */
    @NotBlank(message = "Identifier is required")
    private String identifier;

    /**
     * Type of OTP: EMAIL, PHONE, PASSWORD_RESET, TWO_FACTOR
     */
    @NotBlank(message = "OTP type is required")
    private String type;
}

