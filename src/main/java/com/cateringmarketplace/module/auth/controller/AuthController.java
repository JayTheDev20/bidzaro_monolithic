package com.cateringmarketplace.module.auth.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.module.auth.dto.request.*;
import com.cateringmarketplace.module.auth.dto.response.*;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication and authorization APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        log.info("Registration request received for email: {}", request.getEmail());
        AuthResponse response = authService.register(request, httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Registration successful. Please verify your email."));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns access and refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        log.info("Login request received for identifier: {}", request.getIdentifier());
        AuthResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Generates new access and refresh tokens")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        log.debug("Token refresh request received");
        TokenResponse response = authService.refreshToken(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates the current refresh token")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(refreshToken, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", description = "Invalidates all refresh tokens for the user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> logoutAll(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logoutAll(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out from all devices successfully"));
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP", description = "Sends OTP to email or phone for verification")
    public ResponseEntity<ApiResponse<OTPResponse>> sendOTP(
            @Valid @RequestBody SendOTPRequest request) {
        log.info("OTP send request received for: {}", request.getIdentifier());
        OTPResponse response = authService.sendOTP(request);
        return ResponseEntity.ok(ApiResponse.success(response, "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verifies the OTP sent to email or phone")
    public ResponseEntity<ApiResponse<OTPResponse>> verifyOTP(
            @Valid @RequestBody VerifyOTPRequest request) {
        log.info("OTP verification request received for: {}", request.getIdentifier());
        OTPResponse response = authService.verifyOTP(request);
        return ResponseEntity.ok(ApiResponse.success(response, "OTP verified successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Initiates password reset by sending OTP to email")
    public ResponseEntity<ApiResponse<OTPResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request received for: {}", request.getEmail());
        OTPResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Password reset OTP sent to your email"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets password using OTP verification")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        log.info("Password reset request received for: {}", request.getEmail());
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Changes password for authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.changePassword(request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the current authenticated user's information")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = authService.getCurrentUser(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
