package com.crepusculum.loanapp.travel_loan_manager.controller;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.AdminResetPasswordRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RecordPaymentRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RegisterBorrowerRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerSummaryResponse;
import com.crepusculum.loanapp.travel_loan_manager.service.AdminService;
import com.crepusculum.loanapp.travel_loan_manager.service.BorrowerQueryService;
import com.crepusculum.loanapp.travel_loan_manager.service.PasswordResetService;
import com.crepusculum.loanapp.travel_loan_manager.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final BorrowerQueryService borrowerQueryService;
    private final PdfExportService pdfExportService;
    private final PasswordResetService passwordResetService;

    @PostMapping(value = "/borrowers", consumes = "multipart/form-data")
    public ResponseEntity<String> registerBorrower(
            @ModelAttribute RegisterBorrowerRequest request) {
        String message = adminService.registerBorrower(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/payments")
    public ResponseEntity<String> recordPayment(
            @RequestBody RecordPaymentRequest request) {
        String message = adminService.recordPayment(request);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/borrowers")
    public ResponseEntity<Page<BorrowerSummaryResponse>> getAllBorrowers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<BorrowerSummaryResponse> result = borrowerQueryService.searchBorrowers(
                search, status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/borrowers/{id}/schedule")
    public ResponseEntity<byte[]> downloadSchedule(@PathVariable Long id) {
        return pdfExportService.generateRepaymentSchedulePdf(id);
    }

    @GetMapping("/borrowers/{id}")
    public ResponseEntity<com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerDetailResponse> getBorrowerDetails(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getBorrowerDetails(id));
    }

    @PostMapping("/borrowers/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetBorrowerPassword(
            @PathVariable Long id,
            @RequestBody AdminResetPasswordRequest request) {
        passwordResetService.adminResetPassword(id, request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully. User will be required to change it on next login."));
    }
}