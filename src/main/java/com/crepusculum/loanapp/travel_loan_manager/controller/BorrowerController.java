package com.crepusculum.loanapp.travel_loan_manager.controller;

import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerDashboardResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.service.BorrowerDashboardService;
import com.crepusculum.loanapp.travel_loan_manager.service.PdfGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrower")
@RequiredArgsConstructor
public class BorrowerController {

    private final BorrowerDashboardService dashboardService;
    private final PdfGenerationService  pdfGenerationService;

    @GetMapping("/me")
    public ResponseEntity<BorrowerDashboardResponse> getMyDashboard(
            @AuthenticationPrincipal Borrower borrower) {

        BorrowerDashboardResponse response = dashboardService.getDashboard(borrower);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/schedule")
    public ResponseEntity<byte[]> downloadMySchedule(@AuthenticationPrincipal Borrower borrower) {
        byte[] pdf = pdfGenerationService.generateRepaymentSchedule(borrower);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"My-Repayment-Schedule.pdf\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}