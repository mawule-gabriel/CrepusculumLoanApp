package com.crepusculum.loanapp.travel_loan_manager.dto.request;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record RegisterBorrowerRequest(

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Ghana Card number is required")
        @Size(min = 9, max = 20)
        String ghanaCardNumber,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^233\\d{9}$", message = "Phone must be in format 233xxxxxxxxx")
        String phoneNumber,

        @NotBlank(message = "Home address in Ghana is required")
        String homeAddressGhana,

        @NotBlank(message = "Destination address is required")
        String destinationAddress,

        MultipartFile profilePicture,

        @NotNull(message = "Loan amount is required")
        @DecimalMin(value = "1000.00", message = "Minimum loan amount is GHS 1,000")
        @DecimalMax(value = "200000.00", message = "Maximum loan amount is GHS 200,000")
        BigDecimal loanAmount,

        @Min(value = 11)
        @Max(value = 36)
        Integer monthsDuration,

        @NotBlank(message = "Guarantor name is required")
        String guarantorName,

        @NotBlank(message = "Guarantor phone is required")
        @Pattern(regexp = "^233\\d{9}$", message = "Guarantor phone must be in format 233xxxxxxxxx")
        String guarantorPhone,

        String guarantorRelationship,

        @Email(message = "Invalid email format")
        String email
) {
}