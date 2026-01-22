package com.crepusculum.loanapp.travel_loan_manager;

import com.crepusculum.loanapp.travel_loan_manager.config.TestMailConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestMailConfiguration.class)
class TravelLoanManagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
