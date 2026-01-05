package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.RecordPaymentRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RegisterBorrowerRequest;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import com.crepusculum.loanapp.travel_loan_manager.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerDetailResponse;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerDetailResponse.PaymentResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Payment;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BorrowerService borrowerService;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @Transactional
    public String registerBorrower(RegisterBorrowerRequest request) {
        Borrower borrower = borrowerService.createBorrowerWithGuarantor(request);
        return formatRegistrationMessage(borrower, request);
    }

    @Transactional
    public String recordPayment(RecordPaymentRequest request) {
        Borrower borrower = borrowerService.findById(request.borrowerId());
        Loan loan = validateAndGetLoan(borrower);

        paymentService.recordPayment(
                loan,
                request.amountPaid(),
                request.note(),
                "ADMIN"
        );

        return "Payment recorded successfully";
    }

    private Loan validateAndGetLoan(Borrower borrower) {
        if (borrower.getLoan() == null) {
            throw new IllegalStateException("Borrower does not have an active loan");
        }
        return borrower.getLoan();
    }

    private String formatRegistrationMessage(Borrower borrower, RegisterBorrowerRequest request) {
        String defaultPassword = request.phoneNumber()
                .substring(request.phoneNumber().length() - 8);

        return String.format(
                """
                Borrower registered successfully!
                • Full Name        : %s
                • Phone Number     : %s
                • Loan Amount      : GHS %,.2f
                • Duration         : %d months
                • Monthly Payment  : GHS %,.2f
                • Default Password : %s
                """,
                borrower.getFullName(),
                borrower.getPhoneNumber(),
                borrower.getLoan().getAmount(),
                borrower.getLoan().getMonthsDuration(),
                borrower.getLoan().getMonthlyPayment(),
                defaultPassword
        );
    }

    public BorrowerDetailResponse getBorrowerDetails(Long id) {
        Borrower borrower = borrowerService.findById(id);
        Loan loan = borrower.getLoan();
        
        List<Payment> payments =
                paymentRepository.findByLoanIdOrderByPaymentDateDesc(loan.getId());

        int monthsPaid = paymentRepository.findDistinctPaymentDatesByLoanId(loan.getId()).size();

                List<PaymentResponse> paymentResponses =
                payments.stream()
                .map(p -> new BorrowerDetailResponse.PaymentResponse(
                        p.getAmountPaid(),
                        p.getPaymentDate(),
                        p.getRecordedBy(),
                        null
                ))
                .toList();

        return new BorrowerDetailResponse(
                borrower.getId(),
                borrower.getFullName(),
                borrower.getPhoneNumber(),
                borrower.getGhanaCardNumber(),
                borrower.getHomeAddressGhana(),
                borrower.getDestinationAddress(),
                borrower.getProfilePicturePath(),
                loan.getAmount(),
                loan.getMonthlyPayment(),
                loan.getTotalPaid(),
                loan.getBalance(),
                loan.getStartDate(),
                loan.getEndDate(),
                loan.getMonthsDuration(),
                monthsPaid,
                borrower.getGuarantor() != null ? borrower.getGuarantor().getFullName() : null,
                borrower.getGuarantor() != null ? borrower.getGuarantor().getPhoneNumber() : null,
                borrower.getGuarantor() != null ? borrower.getGuarantor().getRelationship() : null,
                paymentResponses
        );
    }
}