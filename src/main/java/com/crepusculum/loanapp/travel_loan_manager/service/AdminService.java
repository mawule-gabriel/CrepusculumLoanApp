package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.RecordPaymentRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RegisterBorrowerRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerSummaryResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BorrowerService borrowerService;
    private final PaymentService paymentService;
    private final AdminDashboardService dashboardService;

    @Transactional
    public String registerBorrower(RegisterBorrowerRequest request) {
        Borrower borrower = borrowerService.createBorrowerWithGuarantor(request);
        String defaultPassword = request.phoneNumber().substring(request.phoneNumber().length() - 8);

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

    @Transactional
    public String recordPayment(RecordPaymentRequest request) {
        Borrower borrower = borrowerService.findById(request.borrowerId());

        if (borrower.getLoan() == null) {
            throw new IllegalStateException("Borrower does not have an active loan");
        }

        paymentService.recordPayment(
                borrower.getLoan(),
                request.amountPaid(),
                request.note(),
                "ADMIN"
        );

        return "Payment recorded successfully";
    }

    public List<BorrowerSummaryResponse> getAllBorrowerSummaries() {
        return dashboardService.getAllBorrowerSummaries();
    }
}