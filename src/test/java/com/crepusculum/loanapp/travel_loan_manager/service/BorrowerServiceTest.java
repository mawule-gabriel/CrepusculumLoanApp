package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.RegisterBorrowerRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.ImageUploadResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.Guarantor;
import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import com.crepusculum.loanapp.travel_loan_manager.repository.BorrowerRepository;
import com.crepusculum.loanapp.travel_loan_manager.repository.GuarantorRepository;
import com.crepusculum.loanapp.travel_loan_manager.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowerServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;
    @Mock
    private GuarantorRepository guarantorRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private LoanService loanService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BorrowerService borrowerService;

    @Test
    void createBorrowerWithGuarantor_Success() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);

        RegisterBorrowerRequest request = new RegisterBorrowerRequest(
                "John Doe", "GHA-123456789-0", "233201234567",
                "Accra", "London", mockFile,
                new BigDecimal("5000"), 12,
                "Guarantor Name", "233207654321", "Family",
                "john@example.com"
        );

        when(borrowerRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(borrowerRepository.existsByGhanaCardNumber(any())).thenReturn(false);
        when(cloudinaryService.uploadProfilePicture(mockFile)).thenReturn(
                new ImageUploadResponse("http://image.url", "public_id", "jpg", 1024L)
        );
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        Borrower savedBorrower = new Borrower();
        savedBorrower.setId(1L);
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(savedBorrower);

        Loan mockLoan = new Loan();
        when(loanService.createLoan(any(), any(), anyInt())).thenReturn(mockLoan);

        Borrower result = borrowerService.createBorrowerWithGuarantor(request);

        assertNotNull(result);
        verify(borrowerRepository, times(2)).save(any(Borrower.class));
        verify(guarantorRepository).save(any(Guarantor.class));
        verify(loanService).createLoan(any(), eq(new BigDecimal("5000")), eq(12));
    }

    @Test
    void createBorrower_DuplicatePhone_ThrowsException() {
        RegisterBorrowerRequest request = mock(RegisterBorrowerRequest.class);
        when(request.phoneNumber()).thenReturn("233201234567");
        when(borrowerRepository.existsByPhoneNumber("233201234567")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> borrowerService.createBorrowerWithGuarantor(request));
    }

    @Test
    void findById_Success() {
        Borrower borrower = new Borrower();
        borrower.setId(1L);
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));

        Borrower result = borrowerService.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findById_NotFound_ThrowsException() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> borrowerService.findById(1L));
    }
}
