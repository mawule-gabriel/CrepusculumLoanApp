package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 10);
    private static final Font SMALL_FONT = new Font(Font.HELVETICA, 8);

    public byte[] generateRepaymentSchedule(Borrower borrower) {
        Loan loan = borrower.getLoan();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 40, 40, 80, 60);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new PdfHeaderFooter());

            document.open();

            addTitleAndBorrowerInfo(document, borrower, loan);
            addRepaymentTable(document, loan);
            addSummaryAndSignature(document, loan);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }

        return baos.toByteArray();
    }

    private void addTitleAndBorrowerInfo(Document document, Borrower borrower, Loan loan) throws DocumentException {
        Paragraph title = new Paragraph("TRAVEL LOAN REPAYMENT SCHEDULE", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20);

        addInfoCell(infoTable, "Borrower Name:", borrower.getFullName());
        addInfoCell(infoTable, "Phone Number:", borrower.getPhoneNumber());
        addInfoCell(infoTable, "Ghana Card No:", borrower.getGhanaCardNumber());
        addInfoCell(infoTable, "Destination:", borrower.getDestinationAddress());
        addInfoCell(infoTable, "Loan Amount:", String.format("GHS %, .2f", loan.getAmount()));
        addInfoCell(infoTable, "Monthly Payment:", String.format("GHS %, .2f", loan.getMonthlyPayment()));
        addInfoCell(infoTable, "Duration:", loan.getMonthsDuration() + " months");
        addInfoCell(infoTable, "Start Date:", DATE_FORMAT.format(loan.getStartDate()));
        addInfoCell(infoTable, "End Date:", DATE_FORMAT.format(loan.getEndDate()));

        document.add(infoTable);
    }

    private void addInfoCell(PdfPTable table, String label, String value) {
        table.addCell(createCell(label, HEADER_FONT));
        table.addCell(createCell(value, NORMAL_FONT));
    }

    private void addRepaymentTable(Document document, Loan loan) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{10, 25, 25, 25, 15});
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);

        addTableHeader(table, "Month", "Due Date", "Amount Due", "Amount Paid", "Status");

        LocalDate dueDate = loan.getStartDate();
        for (int i = 1; i <= loan.getMonthsDuration(); i++) {
            String status = "Pending";
            if (loan.getTotalPaid().compareTo(loan.getMonthlyPayment().multiply(BigDecimal.valueOf(i))) >= 0) {
                status = "Paid";
            } else if (LocalDate.now().isAfter(dueDate)) {
                status = "Overdue";
            }

            table.addCell(createCell(String.valueOf(i), NORMAL_FONT));
            table.addCell(createCell(DATE_FORMAT.format(dueDate), NORMAL_FONT));
            table.addCell(createCell(String.format("GHS %, .2f", loan.getMonthlyPayment()), NORMAL_FONT));
            table.addCell(createCell(status.equals("Paid") ? String.format("GHS %, .2f", loan.getMonthlyPayment()) : "-", NORMAL_FONT));
            table.addCell(createCell(status, getStatusFont(status)));
        }

        document.add(table);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new Color(200, 200, 255));
            table.addCell(cell);
        }
    }

    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5);
        return cell;
    }

    private Font getStatusFont(String status) {
        return switch (status) {
            case "Paid" -> new Font(Font.HELVETICA, 10, Font.BOLD, Color.GREEN);
            case "Overdue" -> new Font(Font.HELVETICA, 10, Font.BOLD, Color.RED);
            default -> NORMAL_FONT;
        };
    }

    private void addSummaryAndSignature(Document document, Loan loan) throws DocumentException {
        Paragraph summary = new Paragraph();
        summary.add(new Phrase("Total Amount to Repay: ", HEADER_FONT));
        summary.add(new Phrase(String.format("GHS %, .2f", loan.getAmount()), NORMAL_FONT));
        summary.setSpacingBefore(20);
        document.add(summary);

        Paragraph note = new Paragraph("This is a system-generated repayment schedule. For inquiries, contact our office.", SMALL_FONT);
        note.setAlignment(Element.ALIGN_CENTER);
        note.setSpacingBefore(40);
        document.add(note);

        Paragraph signature = new Paragraph("_________________________\nAuthorized Signature", NORMAL_FONT);
        signature.setAlignment(Element.ALIGN_RIGHT);
        signature.setSpacingBefore(30);
        document.add(signature);
    }
}