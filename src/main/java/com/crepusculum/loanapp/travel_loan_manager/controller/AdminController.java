package com.crepusculum.loanapp.travel_loan_manager.controller;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.RecordPaymentRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RegisterBorrowerRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerSummaryResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.service.AdminDashboardService;
import com.crepusculum.loanapp.travel_loan_manager.service.AdminService;
import com.crepusculum.loanapp.travel_loan_manager.service.BorrowerService;
import com.crepusculum.loanapp.travel_loan_manager.service.PdfGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AdminDashboardService adminDashboardService;
    private final BorrowerService borrowerService;
    private final PdfGenerationService pdfGenerationService;

    @PostMapping(value = "/borrowers", consumes = "multipart/form-data")
    public ResponseEntity<String> registerBorrower(@ModelAttribute RegisterBorrowerRequest request) {
        String message = adminService.registerBorrower(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/payments")
    public ResponseEntity<String> recordPayment(@RequestBody RecordPaymentRequest request) {
        String message = adminService.recordPayment(request);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/borrowers")
    public ResponseEntity<Page<BorrowerSummaryResponse>> getAllBorrowers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "On Track,Delayed,Completed") List<String> status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<BorrowerSummaryResponse> result = adminDashboardService.searchBorrowers(
                search, String.join(",", status), page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/borrowers/{id}/schedule")
    public ResponseEntity<byte[]> downloadSchedule(@PathVariable Long id) {
        Borrower borrower = borrowerService.findById(id);
        byte[] pdf = pdfGenerationService.generateRepaymentSchedule(borrower);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"Repayment-Schedule-" + borrower.getPhoneNumber() + ".pdf\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}