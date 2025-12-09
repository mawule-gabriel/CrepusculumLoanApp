package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private final BorrowerService borrowerService;
    private final PdfGenerationService pdfGenerationService;

    public ResponseEntity<byte[]> generateRepaymentSchedulePdf(Long borrowerId) {
        Borrower borrower = borrowerService.findById(borrowerId);
        byte[] pdfContent = pdfGenerationService.generateRepaymentSchedule(borrower);

        String filename = buildPdfFilename(borrower);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    private String buildPdfFilename(Borrower borrower) {
        return String.format("Repayment-Schedule-%s.pdf",
                borrower.getPhoneNumber());
    }
}