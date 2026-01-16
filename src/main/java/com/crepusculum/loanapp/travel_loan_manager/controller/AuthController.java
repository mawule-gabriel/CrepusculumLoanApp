package com.crepusculum.loanapp.travel_loan_manager.controller;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.*;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.JwtResponse;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.RefreshTokenResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.service.AuthService;
import com.crepusculum.loanapp.travel_loan_manager.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.initiatePasswordReset(request.phoneNumber());
        return ResponseEntity.ok(Map.of("message", "If an email is associated with this account, you will receive reset instructions"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal Borrower borrower,
            @Valid @RequestBody ChangePasswordRequest request) {
        passwordResetService.changePassword(borrower.getId(), request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}