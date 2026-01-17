package com.crepusculum.loanapp.travel_loan_manager.dto.response;

public record AdminResetPasswordResponse(
        String message,
        String temporaryPassword
) {
}
