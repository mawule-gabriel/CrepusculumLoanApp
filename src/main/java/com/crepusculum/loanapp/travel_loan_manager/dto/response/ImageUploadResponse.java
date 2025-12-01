package com.crepusculum.loanapp.travel_loan_manager.dto.response;

public record ImageUploadResponse(
        String secureUrl,
        String publicId,
        String format,
        Long size
) {
}