package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.RecordPaymentRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RegisterBorrowerRequest;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BorrowerService borrowerService;
    private final PaymentService paymentService;

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
}