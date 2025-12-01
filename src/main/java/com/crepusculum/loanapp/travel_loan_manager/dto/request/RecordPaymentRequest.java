package com.crepusculum.loanapp.travel_loan_manager.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RecordPaymentRequest(
        @NotNull(message = "Borrower ID is required")
        Long borrowerId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amountPaid,

        String note
) {}