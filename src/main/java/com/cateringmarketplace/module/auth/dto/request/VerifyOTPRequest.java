package com.cateringmarketplace.module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for OTP verification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOTPRequest {

    /**
     * Email or phone number used for OTP
     */
    @NotBlank(message = "Identifier is required")
    private String identifier;

    /**
     * OTP code
     */
    @NotBlank(message = "OTP is required")
    @Size(min = 4, max = 8, message = "OTP must be between 4 and 8 digits")
    @Pattern(regexp = "^\\d+$", message = "OTP must contain only digits")
    private String otp;

    /**
     * Type of OTP: EMAIL, PHONE, PASSWORD_RESET, TWO_FACTOR
     */
    @NotBlank(message = "OTP type is required")
    private String type;
}

