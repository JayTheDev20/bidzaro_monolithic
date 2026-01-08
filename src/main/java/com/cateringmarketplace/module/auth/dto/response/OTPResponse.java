package com.cateringmarketplace.module.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for OTP operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OTPResponse {

    private String verificationId;
    private String message;
    private Integer expiresInSeconds;
    private Boolean verified;

    public static OTPResponse sent(String verificationId, int expiresInSeconds) {
        return OTPResponse.builder()
                .verificationId(verificationId)
                .message("OTP sent successfully")
                .expiresInSeconds(expiresInSeconds)
                .build();
    }

    public static OTPResponse verified() {
        return OTPResponse.builder()
                .verified(true)
                .message("OTP verified successfully")
                .build();
    }

    public static OTPResponse invalid() {
        return OTPResponse.builder()
                .verified(false)
                .message("Invalid or expired OTP")
                .build();
    }
}

