package com.crepusculum.loanapp.travel_loan_manager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ForgotPasswordRequest(
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^233\\d{9}$", message = "Phone must be in format 233xxxxxxxxx")
        String phoneNumber
) {}
