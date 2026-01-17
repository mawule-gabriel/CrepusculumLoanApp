package com.crepusculum.loanapp.travel_loan_manager.dto.request;

import jakarta.validation.constraints.NotNull;

public record AdminResetPasswordRequest(
        @NotNull(message = "Borrower ID is required")
        Long borrowerId,

        String newPassword
) {
}
