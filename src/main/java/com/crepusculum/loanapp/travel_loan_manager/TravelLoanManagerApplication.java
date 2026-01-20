package com.crepusculum.loanapp.travel_loan_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TravelLoanManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelLoanManagerApplication.class, args);
	}

}
