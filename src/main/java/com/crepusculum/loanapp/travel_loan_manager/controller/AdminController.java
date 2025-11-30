package com.crepusculum.loanapp.travel_loan_manager.controller;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.RecordPaymentRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RegisterBorrowerRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerSummaryResponse;
import com.crepusculum.loanapp.travel_loan_manager.service.AdminService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<BorrowerSummaryResponse>> getAllBorrowers() {
        List<BorrowerSummaryResponse> borrowers = adminService.getAllBorrowerSummaries();
        return ResponseEntity.ok(borrowers);
    }
}