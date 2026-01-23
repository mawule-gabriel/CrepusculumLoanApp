package com.crepusculum.loanapp.travel_loan_manager;

import com.crepusculum.loanapp.travel_loan_manager.config.TestMailConfiguration;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.LoginRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RegisterBorrowerRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.JwtResponse;
import com.crepusculum.loanapp.travel_loan_manager.repository.BorrowerRepository;
import com.crepusculum.loanapp.travel_loan_manager.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestMailConfiguration.class)
class FullUserFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminService adminService;

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Test
    void fullUserFlow_RegistrationAndLogin() throws Exception {
        RegisterBorrowerRequest registerRequest = new RegisterBorrowerRequest(
                "Integration Test User", "GHA-999888777-6", "233501234567",
                "Accra", "New York", null,
                new BigDecimal("10000"), 24,
                "Guarantor One", "233507654321", "Friend",
                "test@example.com"
        );

        adminService.registerBorrower(registerRequest);

        String defaultPassword = "01234567";
        LoginRequest loginRequest = new LoginRequest("233501234567", defaultPassword);

        String loginResponseJson = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.role").value("ROLE_BORROWER"))
                .andReturn().getResponse().getContentAsString();

        JwtResponse jwtResponse = objectMapper.readValue(loginResponseJson, JwtResponse.class);
        String accessToken = jwtResponse.accessToken();

        mockMvc.perform(get("/api/borrower/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Integration Test User"));
    }
}
