package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerSummaryResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import com.crepusculum.loanapp.travel_loan_manager.repository.BorrowerRepository;
import com.crepusculum.loanapp.travel_loan_manager.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final BorrowerRepository borrowerRepository;
    private final PaymentRepository paymentRepository;

    public List<BorrowerSummaryResponse> getAllBorrowerSummaries() {
        return borrowerRepository.findAll().stream()
                .filter(b -> b.getLoan() != null)
                .map(this::mapToSummary)
                .toList();
    }

    public Page<BorrowerSummaryResponse> searchBorrowers(
            String search,
            String status,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Borrower> borrowerPage;
        if (search != null && !search.isBlank()) {
            borrowerPage = borrowerRepository.findBySearchTerm(search.trim(), pageable);
        } else {
            borrowerPage = borrowerRepository.findAll(pageable);
        }

        List<String> statusFilters = Arrays.asList(status.split(","));

        List<BorrowerSummaryResponse> allSummaries = borrowerPage.getContent().stream()
                .filter(b -> b.getLoan() != null)
                .map(this::mapToSummary)
                .toList();

        List<BorrowerSummaryResponse> content = allSummaries.stream()
                .filter(summary -> statusFilters.contains(summary.status()))
                .toList();

        return new PageImpl<>(content, pageable, borrowerPage.getTotalElements());
    }

    private BorrowerSummaryResponse mapToSummary(Borrower b) {
        Loan l = b.getLoan();
        List<LocalDate> paymentDates = paymentRepository.findDistinctPaymentDatesByLoanId(l.getId());
        int monthsPaid = paymentDates.size();

        LocalDate expectedNextDue = l.getStartDate().plusMonths(monthsPaid);

        String status;
        if (monthsPaid >= l.getMonthsDuration()) {
            status = "Completed";
        } else if (LocalDate.now().isAfter(expectedNextDue)) {
            status = "Delayed";
        } else {
            status = "On Track";
        }

        return new BorrowerSummaryResponse(
                b.getId(),
                b.getFullName(),
                b.getPhoneNumber(),
                b.getGhanaCardNumber(),
                b.getProfilePicturePath(),
                l.getAmount(),
                l.getMonthlyPayment(),
                l.getTotalPaid(),
                l.getBalance(),
                l.getStartDate(),
                l.getEndDate(),
                monthsPaid,
                l.getMonthsDuration(),
                status
        );
    }
}