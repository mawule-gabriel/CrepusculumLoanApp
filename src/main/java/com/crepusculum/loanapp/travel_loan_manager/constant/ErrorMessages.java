package com.crepusculum.loanapp.travel_loan_manager.constant;

public final class ErrorMessages {
    private ErrorMessages() {}

    public static final String BORROWER_NOT_FOUND = "Borrower not found with id: %d";
    public static final String PHONE_ALREADY_EXISTS = "Phone number already registered";
    public static final String GHANA_CARD_ALREADY_EXISTS = "Ghana Card number already registered";
    public static final String LOAN_NOT_FOUND = "No loan found for this borrower";
    public static final String INVALID_IMAGE = "Invalid image file. Only JPEG, PNG, WebP allowed (max 5MB)";
    public static final String IMAGE_UPLOAD_FAILED = "Failed to upload profile picture";
    public static final String PAYMENT_RECORD_FAILED = "Failed to record payment";
}
