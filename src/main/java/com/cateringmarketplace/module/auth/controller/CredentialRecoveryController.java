package com.cateringmarketplace.module.auth.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.module.auth.service.CredentialRecoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for credential recovery (forgot email/phone).
 */
@RestController
@RequestMapping("/auth/recover")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Credential Recovery", description = "Forgot email/phone recovery APIs")
public class CredentialRecoveryController {

    private final CredentialRecoveryService credentialRecoveryService;

    @PostMapping("/forgot-email")
    @Operation(summary = "Forgot email - Recover using phone",
               description = "If user forgot email, they can enter phone number to receive email via WhatsApp")
    public ResponseEntity<ApiResponse<Map<String, String>>> forgotEmail(
            @RequestParam @NotBlank(message = "Phone number is required") String phone) {
        log.info("Forgot email request for phone: {}", phone);
        Map<String, String> response = credentialRecoveryService.recoverEmailByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(response, "Email sent to your WhatsApp"));
    }

    @PostMapping("/forgot-phone")
    @Operation(summary = "Forgot phone - Recover using email",
               description = "If user forgot phone number, they can enter email to receive phone via email")
    public ResponseEntity<ApiResponse<Map<String, String>>> forgotPhone(
            @RequestParam @NotBlank(message = "Email is required") String email) {
        log.info("Forgot phone request for email: {}", email);
        Map<String, String> response = credentialRecoveryService.recoverPhoneByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response, "Phone number sent to your email"));
    }
}

