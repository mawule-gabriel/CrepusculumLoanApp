package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.constant.ErrorMessages;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RegisterBorrowerRequest;
import com.crepusculum.loanapp.travel_loan_manager.entity.*;
import com.crepusculum.loanapp.travel_loan_manager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;
    private final GuarantorRepository guarantorRepository;
    private final LoanRepository loanRepository;
    private final CloudinaryService cloudinaryService;
    private final LoanService loanService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Borrower createBorrowerWithGuarantor(RegisterBorrowerRequest req) {
        validateBorrowerUniqueness(req);

        String profilePictureUrl = uploadProfilePictureIfPresent(req);
        String defaultPassword = generateDefaultPassword(req.phoneNumber());

        Borrower borrower = buildBorrower(req, profilePictureUrl, defaultPassword);
        borrower = borrowerRepository.save(borrower);

        createGuarantor(req, borrower);

        Loan loan = loanService.createLoan(
                borrower,
                req.loanAmount(),
                req.monthsDuration()
        );
        borrower.setLoan(loan);
        borrowerRepository.save(borrower);

        return borrower;
    }

    public Borrower findById(Long id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrower not found with id: " + id));
    }

    private void validateBorrowerUniqueness(RegisterBorrowerRequest req) {
        if (borrowerRepository.existsByPhoneNumber(req.phoneNumber())) {
            throw new IllegalArgumentException(ErrorMessages.PHONE_ALREADY_EXISTS);
        }
        if (borrowerRepository.existsByGhanaCardNumber(req.ghanaCardNumber())) {
            throw new IllegalArgumentException(ErrorMessages.GHANA_CARD_ALREADY_EXISTS);
        }
    }

    private String uploadProfilePictureIfPresent(RegisterBorrowerRequest req) {
        if (req.profilePicture() != null && !req.profilePicture().isEmpty()) {
            return cloudinaryService.uploadProfilePicture(req.profilePicture()).secureUrl();
        }
        return null;
    }

    private String generateDefaultPassword(String phoneNumber) {
        return phoneNumber.substring(phoneNumber.length() - 8);
    }

    private Borrower buildBorrower(RegisterBorrowerRequest req, String profilePictureUrl, String password) {
        return Borrower.builder()
                .fullName(req.fullName())
                .ghanaCardNumber(req.ghanaCardNumber())
                .phoneNumber(req.phoneNumber())
                .homeAddressGhana(req.homeAddressGhana())
                .destinationAddress(req.destinationAddress())
                .profilePicturePath(profilePictureUrl)
                .password(passwordEncoder.encode(password))
                .role(Role.BORROWER)
                .build();
    }

    private void createGuarantor(RegisterBorrowerRequest req, Borrower borrower) {
        Guarantor guarantor = Guarantor.builder()
                .fullName(req.guarantorName())
                .phoneNumber(req.guarantorPhone())
                .relationship(req.guarantorRelationship())
                .borrower(borrower)
                .build();
        guarantorRepository.save(guarantor);
    }
}