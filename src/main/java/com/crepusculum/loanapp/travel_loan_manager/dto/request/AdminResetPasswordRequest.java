package com.crepusculum.loanapp.travel_loan_manager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminResetPasswordRequest(
        @NotNull(message = "Borrower ID is required")
        Long borrowerId,

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String newPassword
) {}
